package pt.mocktail.kafkareplay.service.json.condition;

import com.jayway.jsonpath.PathNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.ConditionType;

class NotEqualsMatcherTest {

    private final NotEqualsMatcher matcher = new NotEqualsMatcher();

    private Condition notEqualCondition(String value) {
        return Condition.builder()
                .type(ConditionType.NOT_EQUAL)
                .jsonPath("$.request")
                .value(value)
                .build();
    }

    @Test
    void testReturnsTrueWhenValuesDiffer() {
        String json = "{\"request\": \"hello\"}";
        Assertions.assertTrue(matcher.meetsCondition(notEqualCondition("greet"), json));
    }

    @Test
    void testReturnsFalseWhenValuesAreEqual() {
        String json = "{\"request\": \"greet\"}";
        Assertions.assertFalse(matcher.meetsCondition(notEqualCondition("greet"), json));
    }

    @Test
    void testReturnsTrueWhenBothValuesAreEmpty() {
        String json = "{\"request\": \"\"}";
        Assertions.assertTrue(matcher.meetsCondition(notEqualCondition(""), json));
    }

    @Test
    void testReturnsFalseWhenFieldIsEmptyAndValueIsEmpty() {
        String json = "{\"request\": \"\"}";
        Assertions.assertFalse(matcher.meetsCondition(notEqualCondition(""), json));
    }

    @Test
    void testThrowsWhenFieldIsMissing() {
        String json = "{\"other\": \"value\"}";
        Assertions.assertThrows(PathNotFoundException.class,
                () -> matcher.meetsCondition(notEqualCondition("greet"), json));
    }

    @Test
    void testConditionType() {
        Assertions.assertEquals(ConditionType.NOT_EQUAL, matcher.conditionType());
    }
}
