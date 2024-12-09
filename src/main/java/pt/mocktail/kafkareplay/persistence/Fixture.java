package pt.mocktail.kafkareplay.persistence;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class Fixture {
    private String origin;
    private String destination;
    private Collection<FixtureMapping> mappings;
    private Collection<Condition> conditions;
    private String response;
}
