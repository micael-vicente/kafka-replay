package pt.mocktail.kafkareplay.persistence;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class NoDBFixtureRepo implements FixtureRepository {

    private static final Collection<Fixture> REPO;

    public Collection<Fixture> getAllByOrigin(@NotNull String origin) {
        return REPO.stream()
            .filter(fixture -> origin.equals(fixture.getOrigin()))
            .toList();
    }

    static {
        REPO = List.of(
                getEventExampleFixture()
        );
    }

    private static Fixture getEventExampleFixture() {
        return Fixture.builder()
                .origin("topic-d")
                .destination("destination-topic-d")
                .response("""
                        {
                        "eventId": "{eventId}",
                        "eventDate": "2023-11-30T18:09:30.194Z",
                        "eventType": "UPDATED",
                        "entityName": "entity",
                        "entityData": "{\\"field1\\":\\"{field1}\\",\\"field2\\":null,\\"transactionTimestamp\\":\\"2024-08-07T17:57:00.148\\",\\"payment\\":{\\"paymentMethod\\":\\"VISA\\"}}"
                        }""")
                .mappings(List.of(
                    FixtureMapping.builder()
                        .tag("eventId")
                        .type(MappingType.UUID)
                        .build(),
                    FixtureMapping.builder()
                        .tag("field1")
                        .type(MappingType.JSON_PATH)
                        .replacement("$.id")
                        .build()
                ))
                .conditions(List.of(
                    Condition.builder().onEnvelope(true).jsonPath("$.eventType").value("REQUEST_UPDATE").build()
                ))
                .build();
    }
}
