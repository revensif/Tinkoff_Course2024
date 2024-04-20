package edu.java.retry.backoff;

import java.time.Duration;

public interface RetryBackoff {

    Duration calculateRetryDuration(int attempts);
}
