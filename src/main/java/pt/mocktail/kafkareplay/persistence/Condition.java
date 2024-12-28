package pt.mocktail.kafkareplay.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Condition {
    private ConditionType type;
    private String jsonPath;
    private String value;
    private Set<String> values;
    private boolean onEnvelope;
}
