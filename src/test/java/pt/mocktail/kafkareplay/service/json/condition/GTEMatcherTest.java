package pt.mocktail.kafkareplay.service.json.condition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.ConditionType;

import java.util.Set;

class GTEMatcherTest {

    private final GTEMatcher matcher = new GTEMatcher();

    private Condition gteCondition(String target) {
        return Condition.builder()
                .type(ConditionType.GREATER_THAN_EQUAL_TO)
                .jsonPath("$.timestamp")
                .value(target)
                .build();
    }

    @Test
    void testReturnsTrueWhenAttributeEqualsTarget() {
        String json = "{\"timestamp\": \"2020-01-01T00:00:00Z\"}";
        Condition condition = gteCondition("2020-01-01T00:00:00Z");
        Assertions.assertTrue(matcher.meetsCondition(condition, json));
    }

    @Test
    void testReturnsTrueWhenAttributeIsAfterTarget() {
        String json = "{\"timestamp\": \"2021-06-15T12:30:00Z\"}";
        Condition condition = gteCondition("2020-01-01T00:00:00Z");
        Assertions.assertTrue(matcher.meetsCondition(condition, json));
    }

    @Test
    void testReturnsFalseWhenAttributeIsBeforeTarget() {
        String json = "{\"timestamp\": \"2019-12-31T23:59:59Z\"}";
        Condition condition = gteCondition("2020-01-01T00:00:00Z");
        Assertions.assertFalse(matcher.meetsCondition(condition, json));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2021-01-01T00:00:00Z",
            "2020-12-31T23:59:59Z",
            "1999-12-31T23:59:59Z",
            "2099-01-01T00:00:00Z"
    })
    void testReturnsTrueForVariousFutureTargets(String target) {
        String json = "{\"timestamp\": \"2020-01-01T00:00:00Z\"}";
        Condition condition = gteCondition(target);
        Assertions.assertTrue(matcher.meetsCondition(condition, json));
    }

    @Test
    void testThrowsWhenFieldIsMissing() {
        String json = "{\"other\": \"value\"}";
        Condition condition = gteCondition("2020-01-01T00:00:00Z");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> matcher.meetsCondition(condition, json));
    }

    @Test
    void testThrowsWhenAttributeIsNotAParseableTimestamp() {
        String json = "{\"timestamp\": \"not-a-date\"}";
        Condition condition = gteCondition("2020-01-01T00:00:00Z");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> matcher.meetsCondition(condition, json));
    }

    @Test
    void testThrowsWhenTargetIsNotAParseableTimestamp() {
        String json = "{\"timestamp\": \"2020-01-01T00:00:00Z\"}";
        Condition condition = Condition.builder()
                .type(ConditionType.GREATER_THAN_EQUAL_TO)
                .jsonPath("$.timestamp")
                .value("not-a-date")
                .build();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> matcher.meetsCondition(condition, json));
    }

    @Test
    void testThrowsWhenAttributeIsEmpty() {
        String json = "{\"timestamp\": \"\"}";
        Condition condition = gteCondition("2020-01-01T00:00:00Z");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> matcher.meetsCondition(condition, json));
    }

    @Test
    void testConditionType() {
        Assertions.assertEquals(ConditionType.GREATER_THAN_EQUAL_TO, matcher.conditionType());
    }

    @Test
    void testStrategyRoutesGteConditionsToGteMatcher() {
        MatcherStrategy strategy = new MatcherStrategy(Set.of(matcher));
        Condition condition = gteCondition("2020-01-01T00:00:00Z");
        String json = "{\"timestamp\": \"2021-01-01T00:00:00Z\"}";
        Assertions.assertTrue(strategy.meetsCondition(condition, json));
    }
}
