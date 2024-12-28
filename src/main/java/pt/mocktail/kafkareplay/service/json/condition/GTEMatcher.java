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
        //TODO: to be implemented along with others GT, LT, LTE
        String attribute = getAttribute(condition, json);
        ZonedDateTime other = ZonedDateTime.parse(attribute);
        return !other.isBefore(ZonedDateTime.now());
    }
}
