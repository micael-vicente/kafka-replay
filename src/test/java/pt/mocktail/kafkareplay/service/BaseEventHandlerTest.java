package pt.mocktail.kafkareplay.service;

import org.junit.jupiter.api.Test;
import pt.mocktail.kafkareplay.persistence.FixtureRepository;

import java.util.List;

import static org.mockito.Mockito.*;

class BaseEventHandlerTest {

    private final FixtureRepository mockRepository = mock(FixtureRepository.class);
    private final MockerProducer mockerProducer = mock(MockerProducer.class);

    @Test
    void testWhenEventMissingOriginThenFindNoConditionsAndDoNothing() {
        BaseEventHandler handler = new BaseEventHandler(mockerProducer, mockRepository);

        Event event = Event.builder()
                .message("envelop")
                .fieldContainingMessage("message")
                .envelopedMessage(true)
                .build();

        handler.handleEvent(event);

        verify(mockRepository, times(1)).getAllByOrigin(null);
        verifyNoInteractions(mockerProducer);
    }

    @Test
    void testWhenNoConditionsFoundForOriginThenDoNothing() {
        BaseEventHandler handler = new BaseEventHandler(mockerProducer, mockRepository);

        Event event = Event.builder()
                .origin("not_found")
                .message("envelop")
                .fieldContainingMessage("message")
                .envelopedMessage(true)
                .build();

        when(mockRepository.getAllByOrigin("not_found")).thenReturn(List.of());

        handler.handleEvent(event);

        verify(mockRepository, times(1)).getAllByOrigin("not_found");
        verifyNoInteractions(mockerProducer);
    }

}