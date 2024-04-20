package edu.java.configuration.retry;

import java.time.Duration;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "retry", ignoreUnknownFields = false)
public record RetryBackoffConfigurationProperties(
    @Value("${retry.max-attempts}") int maxAttempts,
    @Value("${retry.backoff-type}") String backoffType,
    @Value("${retry.increment}") Duration increment,
    @Value("${retry.base-delay}") Duration baseDelay,
    @Value("${retry.max-delay}") Duration maxDelay,
    @Value("${retry.multiplier}") double multiplier,
    @Value("${retry.statuses}") Set<Integer> statuses
) {
}
