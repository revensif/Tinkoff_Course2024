package edu.java.bot.retry;

import jakarta.annotation.Nullable;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

public interface RetryBuilder {

    Retry build(long maxAttempts, Duration delay, String statusCodes);

    default Predicate<Throwable> getErrorFilter(@Nullable String statusCodes) {
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
