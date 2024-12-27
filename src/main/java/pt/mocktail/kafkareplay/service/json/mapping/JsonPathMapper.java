package pt.mocktail.kafkareplay.service.json.mapping;

import com.jayway.jsonpath.JsonPath;
import org.springframework.stereotype.Component;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

import static pt.mocktail.kafkareplay.service.json.Constants.JSON_END_OBJ;
import static pt.mocktail.kafkareplay.service.json.Constants.JSON_START_OBJ;

/**
 * Replaces a placeholder with the value found somewhere on the json object.
 */
@Component
public class JsonPathMapper implements JsonMapper {
    @Override
    public MappingType mappingType() {
        return MappingType.JSON_PATH;
    }

    @Override
    public String applyMapping(String originMessage, String responseTemplate, FixtureMapping mapping) {
        String replacement = JsonPath.parse(originMessage).read(mapping.getReplacement(), String.class);
        return responseTemplate.replace(JSON_START_OBJ + mapping.getTag() + JSON_END_OBJ, replacement);
    }
}
