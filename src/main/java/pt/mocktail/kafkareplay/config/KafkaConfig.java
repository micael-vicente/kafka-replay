package pt.mocktail.kafkareplay.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.validation.annotation.Validated;

@Data
@EnableKafka
@EnableConfigurationProperties
@Configuration
@Validated
@ConfigurationProperties(prefix = "mock.kafka")
public class KafkaConfig {

    @NotNull
    private String bootstrapServers;
    @NotNull
    private String consumerGroup;
}
