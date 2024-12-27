package pt.mocktail.kafkareplay.service.json.mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

class ConstantMapperTest {

    private final ConstantMapper mapper = new ConstantMapper();

    @Test
    void testConstantMappingAppliedCorrectly() {
        String originMessage = "{\"greeting\": \"Hey there!\"}";
        String responseTemplate = "{\"result\": \"{result}\"}";
        FixtureMapping fixture = FixtureMapping.builder()
            .tag("result")
            .type(MappingType.CONSTANT)
            .replacement("CONSTANT")
            .build();

        String result = mapper.applyMapping(originMessage, responseTemplate, fixture);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("{\"result\": \"CONSTANT\"}", result);
    }

}