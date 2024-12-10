package pt.mocktail.kafkareplay.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import pt.mocktail.kafkareplay.service.Event;
import pt.mocktail.kafkareplay.service.EventRouter;
import pt.mocktail.kafkareplay.service.exception.HandlerNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockerKafkaConsumer {

    private final EventRouter eventRouter;

    @KafkaListener(topics = "#{'${mock.topics}'.split(',')}", groupId = "${mock.kafka.consumer-group}", concurrency = "3")
    public void readAny(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message from topic: {}", topic);

        Event event = Event.builder()
                .origin(topic)
                .message(message)
                .envelopedMessage(true)
                .fieldContainingMessage("$.entityData")
                .build();

        try {
            eventRouter.route("BASE_HANDLER", event);
        } catch (HandlerNotFoundException e) {
            log.warn("No Handler found for event coming from topic: {}", topic);
        }

        log.info("Message from topic: {} has been handled", topic);
    }
}
