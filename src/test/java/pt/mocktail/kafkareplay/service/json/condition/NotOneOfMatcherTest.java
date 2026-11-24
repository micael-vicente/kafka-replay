package pt.mocktail.kafkareplay.service.json.condition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.ConditionType;

import java.util.Set;

class NotOneOfMatcherTest {

    private final NotOneOfMatcher matcher = new NotOneOfMatcher();

    @Test
    void testReturnsTrueWhenValueNotInSet() {
        String json = "{\"request\": \"bye\"}";
        Condition condition = Condition.builder()
                .type(ConditionType.NOT_ONE_OF)
                .jsonPath("$.request")
                .values(Set.of("greet", "hello"))
                .build();
        Assertions.assertTrue(matcher.meetsCondition(condition, json));
    }

    @Test
    void testReturnsFalseWhenValueInSet() {
        String json = "{\"request\": \"greet\"}";
        Condition condition = Condition.builder()
                .type(ConditionType.NOT_ONE_OF)
                .jsonPath("$.request")
                .values(Set.of("greet", "hello"))
                .build();
        Assertions.assertFalse(matcher.meetsCondition(condition, json));
    }

    @Test
    void testReturnsTrueWhenFieldIsMissing() {
        String json = "{\"other\": \"value\"}";
        Condition condition = Condition.builder()
                .type(ConditionType.NOT_ONE_OF)
                .jsonPath("$.request")
                .values(Set.of("greet", "hello"))
                .build();
        Assertions.assertTrue(matcher.meetsCondition(condition, json));
    }

    @Test
    void testReturnsTrueWhenSetIsEmpty() {
        String json = "{\"request\": \"hello\"}";
        Condition condition = Condition.builder()
                .type(ConditionType.NOT_ONE_OF)
                .jsonPath("$.request")
                .values(Set.of())
                .build();
        Assertions.assertTrue(matcher.meetsCondition(condition, json));
    }

    @Test
    void testConditionType() {
        Assertions.assertEquals(ConditionType.NOT_ONE_OF, matcher.conditionType());
    }
}
