package pt.mocktail.kafkareplay.service.json.mapping;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

class JsonPathMapperTest {

    private final JsonPathMapper mapper = new JsonPathMapper();

    @Test
    void testJsonPathMappingAppliedCorrectly() {
        String originMessage = "{\"greeting\": \"Hey there!\"}";
        String expected = JsonPath.parse(originMessage).read("$.greeting", String.class);

        String responseTemplate = "{\"result\": \"{result}\"}";
        FixtureMapping fixture = FixtureMapping.builder()
            .tag("result")
            .type(MappingType.JSON_PATH)
            .replacement("$.greeting")
            .build();

        String result = mapper.applyMapping(originMessage, responseTemplate, fixture);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertNotEquals(originMessage, result);

        String value = JsonPath.parse(result).read("$.result", String.class);

        Assertions.assertNotNull(expected);
        Assertions.assertFalse(expected.isEmpty());
        Assertions.assertEquals(expected, value);

    }

}