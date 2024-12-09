package pt.mocktail.kafkareplay.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import pt.mocktail.kafkareplay.service.FixtureHandler;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockerKafkaConsumer {

    private final FixtureHandler handler;

    @KafkaListener(topics = "#{'${mock.topics}'.split(',')}", groupId = "${mock.kafka.consumer-group}", concurrency = "3")
    public void readAny(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message from topic: {}", topic);
        handler.handleMessage(topic, message);
        log.info("Message from topic: {} has been handled", topic);
    }
}
