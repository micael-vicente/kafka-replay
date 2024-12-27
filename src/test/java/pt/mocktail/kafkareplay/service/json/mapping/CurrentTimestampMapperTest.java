package pt.mocktail.kafkareplay.service.json.mapping;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

import java.time.Instant;

class CurrentTimestampMapperTest {

    private final CurrentTimestampMapper mapper = new CurrentTimestampMapper();

    @Test
    void testTimestampMappingAppliedCorrectly() {
        String originMessage = "{\"greeting\": \"Hey there!\"}";
        String responseTemplate = "{\"result\": \"{result}\"}";
        FixtureMapping fixture = FixtureMapping.builder()
                .tag("result")
                .type(MappingType.CURR_TIMESTAMP)
                .build();

        String result = mapper.applyMapping(originMessage, responseTemplate, fixture);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertNotEquals(originMessage, result);

        String timestamp = JsonPath.parse(result).read("$.result", String.class);

        Assertions.assertNotNull(timestamp);
        Assertions.assertFalse(timestamp.isEmpty());

        Assertions.assertDoesNotThrow(() -> Instant.parse(timestamp));
    }

}