package pt.mocktail.kafkareplay.service;

import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.FixtureRepository;
import pt.mocktail.kafkareplay.service.json.BaseEventHandler;
import pt.mocktail.kafkareplay.service.json.mapping.MappingStrategy;

import java.util.List;

import static org.mockito.Mockito.*;

class BaseEventHandlerTest {

    private final FixtureRepository mockRepository = mock(FixtureRepository.class);
    private final MockerProducer mockerProducer = mock(MockerProducer.class);
    private final MappingStrategy mockMappingStrategy = mock(MappingStrategy.class);

    @Test
    void testWhenEventMissingOriginThenFindNoConditionsAndDoNothing() {
        BaseEventHandler handler = new BaseEventHandler(mockerProducer, mockRepository, mockMappingStrategy);

        Event event = Event.builder()
                .message("envelop")
                .fieldContainingMessage("message")
                .envelopedMessage(true)
                .build();

        handler.handleEvent(event);

        verify(mockRepository, times(1)).getAllByOrigin(null);
        verifyNoInteractions(mockerProducer, mockMappingStrategy);
    }

    @Test
    void testWhenNoConditionsFoundForOriginThenDoNothing() {
        BaseEventHandler handler = new BaseEventHandler(mockerProducer, mockRepository, mockMappingStrategy);

        Event event = Event.builder()
                .origin("not_found")
                .message("envelop")
                .fieldContainingMessage("message")
                .envelopedMessage(true)
                .build();

        when(mockRepository.getAllByOrigin("not_found")).thenReturn(List.of());

        handler.handleEvent(event);

        verify(mockRepository, times(1)).getAllByOrigin("not_found");
        verifyNoInteractions(mockerProducer, mockMappingStrategy);
    }

}