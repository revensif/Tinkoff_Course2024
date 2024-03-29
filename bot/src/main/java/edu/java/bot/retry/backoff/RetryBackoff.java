package edu.java.bot.retry.backoff;

import java.time.Duration;

public interface RetryBackoff {

    Duration calculateRetryDuration(int attempts);
}
