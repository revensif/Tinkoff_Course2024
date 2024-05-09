package edu.java.bot.retry;

import java.time.Duration;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

@Component(value = "exponential")
public class ExponentialRetryBuilder implements RetryBuilder {

    @Override
    public Retry build(long maxAttempts, Duration delay, String statusCodes) {
        return Retry.backoff(maxAttempts, delay)
            .filter(getErrorFilter(statusCodes));
    }
}
