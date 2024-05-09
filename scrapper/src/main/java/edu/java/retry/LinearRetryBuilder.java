package edu.java.retry;

import java.time.Duration;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

@Component(value = "linear")
public class LinearRetryBuilder implements RetryBuilder {

    @Override
    public Retry build(long maxAttempts, Duration delay, String statusCodes) {
        return new LinearRetryType(maxAttempts, delay)
            .filter(getErrorFilter(statusCodes));
    }
}
