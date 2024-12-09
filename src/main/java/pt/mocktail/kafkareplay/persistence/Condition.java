package pt.mocktail.kafkareplay.persistence;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Condition {
    private ConditionType type;
    private String jsonPath;
    private String value;
    private Set<String> values;
    private boolean onEnvelope;
}
