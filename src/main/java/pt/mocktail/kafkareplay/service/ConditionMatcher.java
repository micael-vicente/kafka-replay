package pt.mocktail.kafkareplay.service;

import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.ConditionType;

public interface ConditionMatcher {

    ConditionType conditionType();

    boolean meetsCondition(Condition condition, String json);
}
