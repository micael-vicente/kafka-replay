package pt.mocktail.kafkareplay.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pt.mocktail.kafkareplay.service.MockerProducer;

@Component
@RequiredArgsConstructor
public class MockerKafkaProducer implements MockerProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topicName, String msg) {
        kafkaTemplate.send(topicName, msg);
    }
}
