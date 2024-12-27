package pt.mocktail.kafkareplay.service.json.mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

import java.util.List;
import java.util.Set;

class ForEachMapperTest {

    private final ForEachMapper mapper = new ForEachMapper();

    @Test
    void testForEachMappingAppliedCorrectly() {

        MappingStrategy strategy = new MappingStrategy(Set.of(new JsonPathMapper()));
        mapper.setStrategy(strategy);

        String originMessage = "{\"greetings\": [{\"greeting\":\"Hello there!\"}]}";
        String responseTemplate = "{\"result\": \"[{result}]\"}";
        FixtureMapping fixture = FixtureMapping.builder()
            .tag("result")
            .replacement("$.greetings")
            .type(MappingType.FOR_EACH)
            .innerBlock("{\"response\": \"{greeting}\"}")
            .innerMappings(List.of(
                FixtureMapping.builder()
                    .type(MappingType.JSON_PATH)
                    .tag("greeting")
                    .replacement("$.greetings[i].greeting")
                    .build()))
            .build();

        String result = mapper.applyMapping(originMessage, responseTemplate, fixture);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertNotEquals(originMessage, result);

        Assertions.assertEquals("{\"result\": \"[{\"response\": \"Hello there!\"}]\"}", result);
    }

}