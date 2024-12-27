package pt.mocktail.kafkareplay.service.json.mapping;

import org.springframework.stereotype.Component;
import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

import java.util.EnumMap;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MappingStrategy {

    private final EnumMap<MappingType, JsonMapper> registry;

    public MappingStrategy(Set<JsonMapper> mappers) {
        registry = mappers.stream()
            .collect(Collectors.toMap(
                JsonMapper::mappingType, Function.identity(),
                (o, o2) -> o,
                () -> new EnumMap<>(MappingType.class)));
    }

    public String applyMapping(String originMessage, String responseTemplate, FixtureMapping mapping) {
        return registry.get(mapping.getType()).applyMapping(originMessage, responseTemplate, mapping);
    }
}
