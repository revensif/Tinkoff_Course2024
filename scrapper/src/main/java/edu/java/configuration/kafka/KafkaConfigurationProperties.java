package edu.java.configuration.kafka;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka", ignoreUnknownFields = false)
public record KafkaConfigurationProperties(
    @Value("kafka.topic") String topic,
    @Value("kafka.bootstrap-servers") String bootstrapServers,
    @Value("kafka.client-id") String clientId,
    @Value("kafka.acks-mode") String acksMode,
    @Value("kafka.delivery-timeout") Duration deliveryTimeout,
    @Value("kafka.linger-ms") Integer lingerMs,
    @Value("kafka.batch-size") Integer batchSize,
    @Value("kafka.enable-idempotence") boolean enableIdempotence
) {
}
