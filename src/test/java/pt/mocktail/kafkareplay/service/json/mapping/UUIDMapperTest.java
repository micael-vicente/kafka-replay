package pt.mocktail.kafkareplay.service.json.mapping;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

import java.util.UUID;
import java.util.Set;

class UUIDMapperTest {

    private final UUIDMapper mapper = new UUIDMapper();

    @Test
    void testUUIDMappingProducesValidUuid() {
        String responseTemplate = "{\"result\": \"{result}\"}";
        FixtureMapping fixture = FixtureMapping.builder()
                .tag("result")
                .type(MappingType.UUID)
                .build();

        String result = mapper.applyMapping("{\"other\": \"x\"}", responseTemplate, fixture);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertNotEquals(responseTemplate, result);

        String resultUUID = JsonPath.parse(result).read("$.result", String.class);
        Assertions.assertNotNull(resultUUID);
        Assertions.assertFalse(resultUUID.isEmpty());

        Assertions.assertDoesNotThrow(() -> UUID.fromString(resultUUID));
    }

    @Test
    void testConditionType() {
        Assertions.assertEquals(MappingType.UUID, mapper.mappingType());
    }
}
