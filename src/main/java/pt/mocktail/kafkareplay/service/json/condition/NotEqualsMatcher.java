package pt.mocktail.kafkareplay.service.json.condition;

import org.springframework.stereotype.Component;
import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.ConditionType;

import java.util.Objects;

@Component
public class NotEqualsMatcher extends JsonConditionMatcher {

    @Override
    public ConditionType conditionType() {
        return ConditionType.NOT_EQUAL;
    }

    @Override
    public boolean meetsCondition(Condition condition, String json) {
        String attribute = getAttribute(condition, json);
        return !Objects.equals(attribute, condition.getValue());
    }
}
