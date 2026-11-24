package pt.mocktail.kafkareplay.service.json.mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

import java.util.List;
import java.util.Set;

class ForEachMapperTest {

    private final ForEachMapper mapper = new ForEachMapper(new MappingStrategy(Set.of(new JsonPathMapper())));

    private FixtureMapping forEachMapping(String replacement, String innerBlock, List<FixtureMapping> innerMappings) {
        return FixtureMapping.builder()
                .tag("result")
                .type(MappingType.FOR_EACH)
                .replacement(replacement)
                .innerBlock(innerBlock)
                .innerMappings(innerMappings)
                .build();
    }

    @Test
    void testForEachMappingAppliedCorrectly() {
        String originMessage = "{\"greetings\": [{\"greeting\":\"Hello there!\"}]}";
        String responseTemplate = "{\"result\": \"[{result}]\"}";
        FixtureMapping fixture = forEachMapping(
                "$.greetings",
                "{\"response\": \"{greeting}\"}",
                List.of(
                        FixtureMapping.builder()
                                .type(MappingType.JSON_PATH)
                                .tag("greeting")
                                .replacement("$.greetings[i].greeting")
                                .build()
                ));

        String result = mapper.applyMapping(originMessage, responseTemplate, fixture);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertNotEquals(originMessage, result);

        Assertions.assertEquals("{\"result\": \"[{\"response\": \"Hello there!\"}]\"}", result);
    }

    @Test
    void testForEachMappingRepeatsOncePerElementWithCorrectIndexSubstitution() {
        String originMessage = "{\"items\": [{\"id\":1,\"name\":\"a\"},{\"id\":2,\"name\":\"b\"},{\"id\":3,\"name\":\"c\"}]}";
        String responseTemplate = "{\"results\": \"[{result}]\"}";
        FixtureMapping fixture = forEachMapping(
                "$.items",
                "{\"id\": \"{id}\", \"name\": \"{name}\"}",
                List.of(
                        FixtureMapping.builder()
                                .type(MappingType.JSON_PATH)
                                .tag("id")
                                .replacement("$.items[i].id")
                                .build(),
                        FixtureMapping.builder()
                                .type(MappingType.JSON_PATH)
                                .tag("name")
                                .replacement("$.items[i].name")
                                .build()
                ));

        String result = mapper.applyMapping(originMessage, responseTemplate, fixture);

        Assertions.assertEquals(
                "{\"results\": \"[{\"id\": \"1\", \"name\": \"a\"},{\"id\": \"2\", \"name\": \"b\"},{\"id\": \"3\", \"name\": \"c\"}]\"}",
                result);
    }

    @Test
    void testForEachMappingProducesEmptyResultForEmptyArray() {
        String originMessage = "{\"items\": []}";
        String responseTemplate = "{\"results\": \"[{result}]\"}";
        FixtureMapping fixture = forEachMapping(
                "$.items",
                "{\"id\": \"{id}\"}",
                List.of(
                        FixtureMapping.builder()
                                .type(MappingType.JSON_PATH)
                                .tag("id")
                                .replacement("$.items[i].id")
                                .build()
                ));

        String result = mapper.applyMapping(originMessage, responseTemplate, fixture);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("{\"results\": \"[]\"}", result);
    }

    @Test
    void testForEachMappingWithConstantInnerBlock() {
        String originMessage = "{\"greetings\": [\"a\", \"b\", \"c\"]}";
        String responseTemplate = "{\"results\": \"[{result}]\"}";
        FixtureMapping fixture = forEachMapping(
                "$.greetings",
                "{\"greeting\": \"Hello\"}",
                List.of()
        );

        String result = mapper.applyMapping(originMessage, responseTemplate, fixture);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("{\"results\": \"[{\"greeting\": \"Hello\"},{\"greeting\": \"Hello\"},{\"greeting\": \"Hello\"}]\"}", result);
    }

    @Test
    void testForEachMappingWithoutPlaceholderLeavesTemplateUnchanged() {
        String originMessage = "{\"greetings\": [\"a\"]}";
        String responseTemplate = "{\"result\": \"no-placeholder-here\"}";
        FixtureMapping fixture = forEachMapping(
                "$.greetings",
                "{\"greeting\": \"Hello\"}",
                List.of()
        );

        String result = mapper.applyMapping(originMessage, responseTemplate, fixture);

        Assertions.assertEquals(responseTemplate, result);
    }

    @Test
    void testConditionType() {
        Assertions.assertEquals(MappingType.FOR_EACH, mapper.mappingType());
    }
}
