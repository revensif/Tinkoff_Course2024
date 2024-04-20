package edu.java.retry.backoff;

import java.time.Duration;

public record ExponentialRetryBackoff(Duration baseDelay, Duration maxDelay, double multiplier)
    implements RetryBackoff {

    @Override
    public Duration calculateRetryDuration(int attempts) {
        Duration calculatedDuration = baseDelay.multipliedBy((long) Math.pow(multiplier, attempts - 1));
        return calculatedDuration.compareTo(maxDelay) < 0 ? calculatedDuration : maxDelay;
    }
}
