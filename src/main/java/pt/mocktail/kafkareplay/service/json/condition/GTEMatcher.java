package pt.mocktail.kafkareplay.service.json.condition;

import org.springframework.stereotype.Component;
import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.ConditionType;

import java.time.ZonedDateTime;

@Component
public class GTEMatcher extends JsonConditionMatcher {

    @Override
    public ConditionType conditionType() {
        return ConditionType.GREATER_THAN_EQUAL_TO;
    }

    @Override
    public boolean meetsCondition(Condition condition, String json) {
        String attribute = getAttribute(condition, json);
        ZonedDateTime other = requireTimestamp("attribute", attribute);
        ZonedDateTime target = requireTimestamp("target value", condition.getValue());
        return !other.isBefore(target);
    }

    private ZonedDateTime requireTimestamp(String what, String value) {
        try {
            return ZonedDateTime.parse(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cannot parse " + what + ": '" + value + "'", e);
        }
    }
}
