package pt.mocktail.kafkareplay.service.json.condition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.ConditionType;

class EqualsMatcherTest {

    private final EqualsMatcher matcher = new EqualsMatcher();

    private Condition equalCondition(String value) {
        return Condition.builder()
                .type(ConditionType.EQUAL)
                .jsonPath("$.request")
                .value(value)
                .build();
    }

    @Test
    void testMatchesWhenEqual() {
        String json = "{\"request\": \"greet\"}";
        Assertions.assertTrue(matcher.meetsCondition(equalCondition("greet"), json));
    }

    @Test
    void testDoesNotMatchWhenNotEqual() {
        String json = "{\"request\": \"hello\"}";
        Assertions.assertFalse(matcher.meetsCondition(equalCondition("greet"), json));
    }

    @Test
    void testMatchesWhenBothValuesAreEmpty() {
        String json = "{\"request\": \"\"}";
        Assertions.assertTrue(matcher.meetsCondition(equalCondition(""), json));
    }

    @Test
    void testThrowsWhenFieldIsMissing() {
        String json = "{\"other\": \"value\"}";
        Assertions.assertThrows(com.jayway.jsonpath.PathNotFoundException.class,
                () -> matcher.meetsCondition(equalCondition("greet"), json));
    }

    @Test
    void testConditionType() {
        Assertions.assertEquals(ConditionType.EQUAL, matcher.conditionType());
    }
}
