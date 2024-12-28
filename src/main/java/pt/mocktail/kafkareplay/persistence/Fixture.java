package pt.mocktail.kafkareplay.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fixture {
    private String origin;
    private String destination;
    private Collection<FixtureMapping> mappings;
    private Collection<Condition> conditions;
    private String response;
}
