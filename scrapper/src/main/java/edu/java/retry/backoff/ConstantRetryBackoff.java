package edu.java.retry.backoff;

import java.time.Duration;

public record ConstantRetryBackoff(Duration baseDelay) implements RetryBackoff {

    @Override
    public Duration calculateRetryDuration(int attempts) {
        return baseDelay;
    }
}
