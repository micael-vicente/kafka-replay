package pt.mocktail.kafkareplay.service.json.mapping;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

class JsonPathMapperTest {

    private final JsonPathMapper mapper = new JsonPathMapper();

    private FixtureMapping mapping(String replacement) {
        return FixtureMapping.builder()
                .tag("result")
                .type(MappingType.JSON_PATH)
                .replacement(replacement)
                .build();
    }

    @Test
    void testJsonPathMappingAppliedCorrectly() {
        String originMessage = "{\"greeting\": \"Hey there!\"}";
        String responseTemplate = "{\"result\": \"{result}\"}";

        String result = mapper.applyMapping(originMessage, responseTemplate, mapping("$.greeting"));

        Assertions.assertEquals("{\"result\": \"Hey there!\"}", result);
    }

    @Test
    void testJsonPathMappingReplacesEmptyValue() {
        String originMessage = "{\"greeting\": \"\"}";
        String responseTemplate = "{\"result\": \"{result}\"}";

        String result = mapper.applyMapping(originMessage, responseTemplate, mapping("$.greeting"));

        Assertions.assertEquals("{\"result\": \"\"}", result);
    }

    @Test
    void testJsonPathMappingThrowsWhenPathIsInvalid() {
        String originMessage = "{\"greeting\": \"Hey there!\"}";
        String responseTemplate = "{\"result\": \"{result}\"}";

        Assertions.assertThrows(com.jayway.jsonpath.PathNotFoundException.class,
                () -> mapper.applyMapping(originMessage, responseTemplate, mapping("$.does.not.exist")));
    }

    @Test
    void testJsonPathMappingThrowsWhenFieldIsMissing() {
        String originMessage = "{\"other\": \"value\"}";
        String responseTemplate = "{\"result\": \"{result}\"}";

        Assertions.assertThrows(com.jayway.jsonpath.PathNotFoundException.class,
                () -> mapper.applyMapping(originMessage, responseTemplate, mapping("$.greeting")));
    }

    @Test
    void testConditionType() {
        Assertions.assertEquals(MappingType.JSON_PATH, mapper.mappingType());
    }
}
