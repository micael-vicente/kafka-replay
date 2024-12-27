package pt.mocktail.kafkareplay.service.json.mapping;

import org.springframework.stereotype.Component;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

import java.util.UUID;

import static pt.mocktail.kafkareplay.service.json.Constants.JSON_END_OBJ;
import static pt.mocktail.kafkareplay.service.json.Constants.JSON_START_OBJ;

@Component
public class UUIDMapper implements JsonMapper {

    @Override
    public MappingType mappingType() {
        return MappingType.UUID;
    }

    @Override
    public String applyMapping(String originMessage, String responseTemplate, FixtureMapping mapping) {
        return responseTemplate.replace(JSON_START_OBJ + mapping.getTag() + JSON_END_OBJ, UUID.randomUUID().toString());
    }
}
