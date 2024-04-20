package edu.java.retry.backoff;

import java.time.Duration;

public record LinearRetryBackoff(Duration baseDelay, Duration maxDelay, Duration increment) implements RetryBackoff {

    @Override
    public Duration calculateRetryDuration(int attempts) {
        Duration calculatedDuration = baseDelay.plus(increment.multipliedBy(attempts - 1));
        return calculatedDuration.compareTo(maxDelay) < 0 ? calculatedDuration : maxDelay;
    }
}
