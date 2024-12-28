package pt.mocktail.kafkareplay.service;

import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.ConditionType;

public interface ConditionMatcher {

    /**
     * Qualifier of the condition matcher.
     * @return the {@link ConditionType} being tested
     */
    ConditionType conditionType();

    /**
     * Checks whether the condition specified through {@link Condition} is met on given json document.
     * @param condition the details of the condition
     * @param json the json to test
     * @return true if condition is met
     */
    boolean meetsCondition(Condition condition, String json);
}
