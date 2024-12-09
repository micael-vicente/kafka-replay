package pt.mocktail.kafkareplay.persistence;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class FixtureMapping {
    private MappingType type;
    private String tag;
    private Collection<FixtureMapping> innerMappings;
    private String innerBlock;
    private String replacement;
}
