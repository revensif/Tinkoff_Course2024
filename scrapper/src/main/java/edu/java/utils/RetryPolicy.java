package edu.java.utils;

import java.time.Duration;

public record RetryPolicy(
    String backoffType,
    int maxAttempts,
    Duration delay,
    String statuses
) {
}
