package edu.java.bot.configuration.kafka;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka", ignoreUnknownFields = false)
public record KafkaConfigurationProperties(
    @Value("kafka.bootstrap-servers") String bootstrapServers,
    @Value("kafka.group-id") String groupId,
    @Value("kafka.auto-offset-reset") String autoOffsetReset,
    @Value("kafka.max-poll-interval-ms") Integer maxPollIntervalMs,
    @Value("kafka.enable-auto-commit") boolean enableAutoCommit,
    @Value("kafka.concurrency") Integer concurrency,
    @NotNull
    @Value("kafka.dlq") DeadLetterQueue dlq
) {
    public record DeadLetterQueue(
        @Value("kafka.dlq.topic") String topic,
        @Value("kafka.dlq.replications") Integer replications,
        @Value("kafka.dlq.partitions") Integer partitions,
        @Value("kafka.dlq.acks-mode") String acksMode,
        @Value("kafka.dlq.delivery-timeout") Duration deliveryTimeout,
        @Value("kafka.dlq.linger-ms") Integer lingerMs,
        @Value("kafka.dlq.batch-size") Integer batchSize
    ) {
    }
}
