package edu.java.utils;

import edu.java.retry.LinearRetryType;
import jakarta.annotation.Nullable;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@UtilityClass
public class RetryUtils {

    private static final long DEFAULT_ATTEMPTS = 3;
    private static final Duration DEFAULT_DELAY = Duration.ofSeconds(1);
    private static final String CONSTANT = "constant";
    private static final String LINEAR = "linear";
    private static final String EXPONENTIAL = "exponential";

    private Retry getConstantRetry(long maxAttempts, Duration delay, Predicate<Throwable> filter) {
        return Retry.fixedDelay(maxAttempts, delay)
            .filter(filter);
    }

    private Retry getLinearRetry(long maxAttempts, Duration delay, Predicate<Throwable> filter) {
        return new LinearRetryType(maxAttempts, delay)
            .filter(filter);
    }

    private Retry getExponentialRetry(long maxAttempts, Duration delay, Predicate<Throwable> filter) {
        return Retry.backoff(maxAttempts, delay)
            .filter(filter);
    }

    public static Retry getRetry(RetryPolicy retryProperties) {
        if (retryProperties == null) {
            return Retry.max(0);
        }
        Predicate<Throwable> filter = getErrorFilter(retryProperties.statuses());
        long maxAttempts = retryProperties.maxAttempts() == 0 ? DEFAULT_ATTEMPTS : retryProperties.maxAttempts();
        Duration delay = retryProperties.delay().isZero() ? DEFAULT_DELAY : retryProperties.delay();
        switch (retryProperties.backoffType()) {
            case CONSTANT -> {
                return getConstantRetry(maxAttempts, delay, filter);
            }
            case LINEAR -> {
                return getLinearRetry(maxAttempts, delay, filter);
            }
            case EXPONENTIAL -> {
                return getExponentialRetry(maxAttempts, delay, filter);
            }
            default -> throw new IllegalArgumentException(
                "Invalid retry type, should be constant, linear, exponential"
            );
        }
    }

    private Predicate<Throwable> getErrorFilter(@Nullable String statusCodes) {
        if (statusCodes == null || statusCodes.isEmpty()) {
            return throwable -> true;
        }
        String[] statusesRange = statusCodes.split("-");
        int firstCode = Integer.parseInt(statusesRange[0]);
        int lastCode = Integer.parseInt(statusesRange[1]);
        Set<Integer> statuses = new HashSet<>();
        for (int i = firstCode; i <= lastCode; i++) {
            statuses.add(i);
        }
        return throwable -> throwable instanceof WebClientResponseException
            && statuses.contains(((WebClientResponseException) throwable).getStatusCode().value());
    }
}
