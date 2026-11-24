package pt.mocktail.kafkareplay.service.json.condition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.ConditionType;

import java.util.Set;

class OneOfMatcherTest {

    private final OneOfMatcher matcher = new OneOfMatcher();

    @Test
    void testReturnsTrueWhenValueInSet() {
        String json = "{\"request\": \"hello\"}";
        Condition condition = Condition.builder()
                .type(ConditionType.ONE_OF)
                .jsonPath("$.request")
                .values(Set.of("greet", "hello"))
                .build();
        Assertions.assertTrue(matcher.meetsCondition(condition, json));
    }

    @Test
    void testReturnsFalseWhenValueNotInSet() {
        String json = "{\"request\": \"bye\"}";
        Condition condition = Condition.builder()
                .type(ConditionType.ONE_OF)
                .jsonPath("$.request")
                .values(Set.of("greet", "hello"))
                .build();
        Assertions.assertFalse(matcher.meetsCondition(condition, json));
    }

    @Test
    void testReturnsFalseWhenFieldIsMissing() {
        String json = "{\"other\": \"value\"}";
        Condition condition = Condition.builder()
                .type(ConditionType.ONE_OF)
                .jsonPath("$.request")
                .values(Set.of("greet", "hello"))
                .build();
        Assertions.assertFalse(matcher.meetsCondition(condition, json));
    }

    @Test
    void testReturnsFalseWhenSetIsEmpty() {
        String json = "{\"request\": \"hello\"}";
        Condition condition = Condition.builder()
                .type(ConditionType.ONE_OF)
                .jsonPath("$.request")
                .values(Set.of())
                .build();
        Assertions.assertFalse(matcher.meetsCondition(condition, json));
    }

    @Test
    void testReturnsTrueWhenSetIsEmptyAndFieldIsEmpty() {
        String json = "{\"request\": \"\"}";
        Condition condition = Condition.builder()
                .type(ConditionType.ONE_OF)
                .jsonPath("$.request")
                .values(Set.of())
                .build();
        Assertions.assertTrue(matcher.meetsCondition(condition, json));
    }

    @Test
    void testConditionType() {
        Assertions.assertEquals(ConditionType.ONE_OF, matcher.conditionType());
    }
}
