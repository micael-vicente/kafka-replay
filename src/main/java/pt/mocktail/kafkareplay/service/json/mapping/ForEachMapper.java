package pt.mocktail.kafkareplay.service.json.mapping;

import com.jayway.jsonpath.JsonPath;
import lombok.Setter;
import org.springframework.stereotype.Component;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;
import pt.mocktail.kafkareplay.service.json.Constants;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pt.mocktail.kafkareplay.service.json.Constants.JSON_ARRAY_INDEX;
import static pt.mocktail.kafkareplay.service.json.Constants.JSON_END_ARRAY;
import static pt.mocktail.kafkareplay.service.json.Constants.JSON_END_OBJ;
import static pt.mocktail.kafkareplay.service.json.Constants.JSON_FIELD_DELIMITER;
import static pt.mocktail.kafkareplay.service.json.Constants.JSON_START_OBJ;

/**
 * Replaces a placeholder with an array which will have its structure defined through {@link FixtureMapping}.
 * The resulting array will have as many entries as there are in the original array.
 */
@Setter
@Component
public class ForEachMapper implements JsonMapper {

    private MappingStrategy strategy;

    @Override
    public MappingType mappingType() {
        return MappingType.FOR_EACH;
    }

    @Override
    public String applyMapping(String originMessage, String responseTemplate, FixtureMapping mapping) {
        int repetitions = JsonPath.parse(originMessage).read(mapping.getReplacement() + Constants.JSON_ARRAY_LENGTH, Integer.class);
        String replacement = IntStream.range(0, repetitions)
                .mapToObj(i -> {
                    String replacingBlock = mapping.getInnerBlock();
                    for(FixtureMapping m : mapping.getInnerMappings()) {
                        FixtureMapping current = FixtureMapping.builder()
                                .tag(m.getTag())
                                .type(m.getType())
                                .replacement(m.getReplacement()
                                    .replace(JSON_ARRAY_INDEX, Constants.JSON_START_ARRAY + i + JSON_END_ARRAY))
                                .build();
                        replacingBlock = strategy.applyMapping(originMessage, replacingBlock, current);
                    }
                    return replacingBlock;
                })
                .collect(Collectors.joining(JSON_FIELD_DELIMITER));
        return responseTemplate.replace(JSON_START_OBJ + mapping.getTag() + JSON_END_OBJ, replacement);
    }
}
