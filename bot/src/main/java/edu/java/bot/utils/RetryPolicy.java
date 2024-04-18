package edu.java.bot.utils;

import java.time.Duration;

public record RetryPolicy(
    String backoffType,
    int maxAttempts,
    Duration delay,
    String statuses
) {
}
