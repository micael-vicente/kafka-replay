package pt.mocktail.kafkareplay.service.json.condition;

import org.springframework.stereotype.Component;
import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.persistence.ConditionType;

@Component
public class OneOfMatcher extends JsonConditionMatcher {

    @Override
    public ConditionType conditionType() {
        return ConditionType.ONE_OF;
    }

    @Override
    public boolean meetsCondition(Condition condition, String json) {
        String attribute = getAttribute(condition, json);
        return condition.getValues().contains(attribute);
    }
}
