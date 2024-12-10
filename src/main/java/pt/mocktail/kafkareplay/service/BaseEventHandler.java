package pt.mocktail.kafkareplay.service;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pt.mocktail.kafkareplay.persistence.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseEventHandler implements EventHandler {
    private final MockerProducer producer;
    private final FixtureRepository repo;

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

        ConditionType type = Optional.ofNullable(condition.getType())
            .orElse(ConditionType.EQUAL);
        String attribute = JsonPath.parse(json).read(condition.getJsonPath(), String.class);

        switch (type) {
            case EQUAL -> {
                return Objects.equals(attribute, condition.getValue());
            }
            case ONE_OF -> {
                return condition.getValues().contains(attribute);
            }
            case NOT_EQUAL -> {
                return !Objects.equals(attribute, condition.getValue());
            }
            case NOT_ONE_OF -> {
                return !condition.getValues().contains(attribute);
            }
            case GREATER_THAN_EQUAL_TO -> {
                ZonedDateTime other = ZonedDateTime.parse(attribute);
                return !other.isBefore(ZonedDateTime.now());
            }
            default -> {
                return false;
            }
        }
    }

    private String applyMappings(String originMessage, String responseTemplate, Collection<FixtureMapping> mappings) {
        AtomicReference<String> result = new AtomicReference<>(responseTemplate);
        mappings.forEach(
                mapping -> result.set(applyMapping(originMessage, result.get(), mapping))
        );

        return result.get();
    }

    private String applyMapping(String originMessage, String responseTemplate, FixtureMapping mapping) {
        String result = responseTemplate;

        switch (mapping.getType()) {
            case UUID -> result = responseTemplate.replace("{" + mapping.getTag() + "}", UUID.randomUUID().toString());
            case CURR_TIMESTAMP -> result = responseTemplate.replace("{" + mapping.getTag() + "}", Instant.now().toString());
            case CONSTANT -> result = responseTemplate.replace("{" + mapping.getTag() + "}", mapping.getReplacement());
            case JSON_PATH -> {
                String replacement = JsonPath.parse(originMessage).read(mapping.getReplacement(), String.class);
                result = responseTemplate.replace("{" + mapping.getTag() + "}", replacement);
            }
            case FOR_EACH -> {
                int repetitions = JsonPath.parse(originMessage).read(mapping.getReplacement() + ".length()", Integer.class);
                String replacement = IntStream.range(0, repetitions)
                    .mapToObj(i -> {
                        String replacingBlock = mapping.getInnerBlock();
                        for(FixtureMapping m : mapping.getInnerMappings()) {
                            FixtureMapping current = FixtureMapping.builder()
                                    .tag(m.getTag())
                                    .type(m.getType())
                                    .replacement(m.getReplacement().replace("[i]", "[" + i + "]"))
                                    .build();
                            replacingBlock = applyMapping(originMessage, replacingBlock, current);
                        }
                        return replacingBlock;
                    })
                    .collect(Collectors.joining(","));
                result = responseTemplate.replace("{" + mapping.getTag() + "}", replacement);
            }
        }

        return result;
    }

    private String getMessage(Event event) {
        if(event.isEnvelopedMessage()) {
            return JsonPath.parse(event.getMessage()).read(event.getFieldContainingMessage(), String.class);
        } else {
            return event.getMessage();
        }
    }
}
