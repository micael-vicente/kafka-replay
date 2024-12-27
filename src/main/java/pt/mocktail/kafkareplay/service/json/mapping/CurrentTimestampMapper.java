package pt.mocktail.kafkareplay.service.json.mapping;

import org.springframework.stereotype.Component;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

import java.time.Instant;

import static pt.mocktail.kafkareplay.service.json.Constants.JSON_END_OBJ;
import static pt.mocktail.kafkareplay.service.json.Constants.JSON_START_OBJ;

/**
 * Replaces a placeholder with the current timestamp.
 */
@Component
public class CurrentTimestampMapper implements JsonMapper {
    @Override
    public MappingType mappingType() {
        return MappingType.CURR_TIMESTAMP;
    }

    @Override
    public String applyMapping(String originMessage, String responseTemplate, FixtureMapping mapping) {
        return responseTemplate.replace(JSON_START_OBJ + mapping.getTag() + JSON_END_OBJ, Instant.now().toString());
    }
}
