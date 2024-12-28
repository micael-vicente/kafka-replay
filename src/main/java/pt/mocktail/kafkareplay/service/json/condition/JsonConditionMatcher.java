package pt.mocktail.kafkareplay.service.json.condition;

import com.jayway.jsonpath.JsonPath;
import pt.mocktail.kafkareplay.persistence.Condition;
import pt.mocktail.kafkareplay.service.ConditionMatcher;

public abstract class JsonConditionMatcher implements ConditionMatcher {

    protected String getAttribute(Condition condition, String json) {
        return JsonPath.parse(json).read(condition.getJsonPath(), String.class);
    }
}
