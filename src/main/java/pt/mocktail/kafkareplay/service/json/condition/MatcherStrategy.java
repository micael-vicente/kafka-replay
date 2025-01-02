package pt.mocktail.kafkareplay.service.json.condition;

import org.springframework.stereotype.Component;
import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.ConditionType;

import java.util.EnumMap;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MatcherStrategy {

    private final EnumMap<ConditionType, JsonConditionMatcher> registry;

    public MatcherStrategy(Set<JsonConditionMatcher> mappers) {
        registry = mappers.stream()
            .collect(Collectors.toMap(
                JsonConditionMatcher::conditionType, Function.identity(),
                (o, o2) -> o,
                () -> new EnumMap<>(ConditionType.class)));
    }

    public boolean meetsCondition(Condition condition, String json) {
        return registry.get(condition.getType()).meetsCondition(condition, json);
    }
}
