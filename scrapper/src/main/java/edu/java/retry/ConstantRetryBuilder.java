package edu.java.retry;

import java.time.Duration;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

@Component(value = "constant")
public class ConstantRetryBuilder implements RetryBuilder {

    @Override
    public Retry build(long maxAttempts, Duration delay, String statusCodes) {
        return Retry.fixedDelay(maxAttempts, delay)
            .filter(getErrorFilter(statusCodes));
    }
}
