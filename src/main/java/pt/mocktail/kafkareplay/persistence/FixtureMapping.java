package pt.mocktail.kafkareplay.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixtureMapping {
    private MappingType type;
    private String tag;
    private Collection<FixtureMapping> innerMappings;
    private String innerBlock;
    private String replacement;
}
