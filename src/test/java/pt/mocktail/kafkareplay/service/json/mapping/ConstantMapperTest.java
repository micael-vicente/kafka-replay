package pt.mocktail.kafkareplay.service.json.mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

class ConstantMapperTest {

    private final ConstantMapper mapper = new ConstantMapper();

    @Test
    void testConstantMappingAppliedCorrectly() {
        String responseTemplate = "{\"result\": \"{result}\"}";
        FixtureMapping fixture = FixtureMapping.builder()
                .tag("result")
                .type(MappingType.CONSTANT)
                .replacement("CONSTANT")
                .build();

        String result = mapper.applyMapping("{\"other\": \"x\"}", responseTemplate, fixture);

        Assertions.assertEquals("{\"result\": \"CONSTANT\"}", result);
    }

    @Test
    void testConstantMappingWithEmptyReplacement() {
        String responseTemplate = "{\"result\": \"{result}\"}";
        FixtureMapping fixture = FixtureMapping.builder()
                .tag("result")
                .type(MappingType.CONSTANT)
                .replacement("")
                .build();

        String result = mapper.applyMapping("{\"other\": \"x\"}", responseTemplate, fixture);

        Assertions.assertEquals("{\"result\": \"\"}", result);
    }

    @Test
    void testConstantMappingLeavesTemplateUnchangedWhenTagMissing() {
        String responseTemplate = "{\"result\": \"no-placeholder\"}";
        FixtureMapping fixture = FixtureMapping.builder()
                .tag("result")
                .type(MappingType.CONSTANT)
                .replacement("CONSTANT")
                .build();

        String result = mapper.applyMapping("{\"other\": \"x\"}", responseTemplate, fixture);

        Assertions.assertEquals(responseTemplate, result);
    }

    @Test
    void testConditionType() {
        Assertions.assertEquals(MappingType.CONSTANT, mapper.mappingType());
    }
}
