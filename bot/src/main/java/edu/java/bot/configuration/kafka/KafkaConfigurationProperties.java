package edu.java.bot.configuration.kafka;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka", ignoreUnknownFields = false)
public record KafkaConfigurationProperties(
    String bootstrapServers,
    String groupId,
    String autoOffsetReset,
    Integer maxPollIntervalMs,
    boolean enableAutoCommit,
    Integer concurrency,
    @NotNull
    DeadLetterQueue dlq
) {
    public record DeadLetterQueue(
        String topic,
        Integer replications,
        Integer partitions,
        String acksMode,
        Duration deliveryTimeout,
        Integer lingerMs,
        Integer batchSize
    ) {
    }
}
