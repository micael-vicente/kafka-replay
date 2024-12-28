package pt.mocktail.kafkareplay.service.json;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.Fixture;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.FixtureRepository;
import pt.mocktail.kafkareplay.service.Event;
import pt.mocktail.kafkareplay.service.EventHandler;
import pt.mocktail.kafkareplay.service.MockerProducer;
import pt.mocktail.kafkareplay.service.json.condition.MatcherStrategy;
import pt.mocktail.kafkareplay.service.json.mapping.MappingStrategy;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseEventHandler implements EventHandler {
    private final MockerProducer producer;
    private final FixtureRepository repo;
    private final MappingStrategy jsonMapper;
    private final MatcherStrategy conditionMatcher;

    @Override
    public String getQualifier() {
        return "BASE_HANDLER";
    }

    @Override
    public void handleEvent(Event event) {
        Collection<Fixture> fixtures = repo.getAllByOrigin(event.getOrigin());
        fixtures.forEach(f -> handleFixture(f, event));
    }

    private void handleFixture(Fixture fixture, Event event) {

        String message = getMessage(event);
        boolean proceed = true;

        if(!CollectionUtils.isEmpty(fixture.getConditions())) {
            Collection<Condition> envelopConditions = fixture.getConditions().stream()
                    .filter(Condition::isOnEnvelope)
                    .collect(Collectors.toSet());

            proceed = envelopConditions.stream()
                    .allMatch(condition -> conditionIsMet(condition, event.getMessage()));

            if(!proceed) {
                log.info("Envelop conditions not met for message from origin: {}", fixture.getOrigin());
                return;
            }

            log.info("Envelop conditions met for message from origin: {}", fixture.getOrigin());
            Collection<Condition> innerMessageConditions = fixture.getConditions().stream()
                    .filter(not(Condition::isOnEnvelope))
                    .collect(Collectors.toSet());

            proceed = innerMessageConditions.stream()
                    .allMatch(condition -> conditionIsMet(condition, message));
        }

        if(proceed) {
            log.info("Sub conditions met for message from origin: {}", fixture.getOrigin());
            String response = applyMappings(message, fixture.getResponse(), fixture.getMappings());
            producer.sendMessage(fixture.getDestination(), response);
        } else {
            log.info("Sub conditions not met for message from origin: {}", fixture.getOrigin());
        }
    }

    private boolean conditionIsMet(Condition condition, String json) {
        return conditionMatcher.meetsCondition(condition, json);
    }

    private String applyMappings(String originMessage, String responseTemplate, Collection<FixtureMapping> mappings) {
        AtomicReference<String> result = new AtomicReference<>(responseTemplate);
        mappings.forEach(
            mapping -> result.set(applyMapping(originMessage, result.get(), mapping))
        );

        return result.get();
    }

    private String applyMapping(String originMessage, String responseTemplate, FixtureMapping mapping) {
        return jsonMapper.applyMapping(originMessage, responseTemplate, mapping);
    }

    private String getMessage(Event event) {
        if(event.isEnvelopedMessage()) {
            return JsonPath.parse(event.getMessage()).read(event.getFieldContainingMessage(), String.class);
        } else {
            return event.getMessage();
        }
    }
}
