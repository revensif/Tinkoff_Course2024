package edu.java.bot.retry;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import org.reactivestreams.Publisher;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class LinearRetryType extends Retry {

    private final long maxAttempts;
    private final Duration delay;
    private final Predicate<Throwable> errorFilter;
    private final BiFunction<LinearRetryType, RetrySignal, Throwable> backoffExceptionGenerator =
        (builder, rs) -> Exceptions.retryExhausted(
            "Retries exhausted: " + (rs.totalRetries() + "/" + builder.maxAttempts), rs.failure());

    public LinearRetryType(long maxAttempts, Duration delay) {
        this.maxAttempts = maxAttempts;
        this.delay = delay;
        this.errorFilter = null;
    }

    public LinearRetryType filter(Predicate<Throwable> errorFilter) {
        return new LinearRetryType(this.maxAttempts, this.delay, errorFilter);
    }

    private LinearRetryType(long maxAttempts, Duration delay, Predicate<Throwable> errorFilter) {
        this.maxAttempts = maxAttempts;
        this.delay = delay;
        this.errorFilter = errorFilter;
    }

    @Override
    public Publisher<?> generateCompanion(Flux<RetrySignal> t) {
        return Flux.deferContextual((cv) -> t.contextWrite(cv).concatMap((retryWhenState) -> {
            var copy = retryWhenState.copy();
            Throwable currentFailure = copy.failure();
            long iteration = copy.totalRetries();
            if (currentFailure == null) {
                return Mono.error(new IllegalStateException("Retry.RetrySignal#failure() not expected to be null"));
            } else if (!this.errorFilter.test(currentFailure)) {
                return Mono.error(currentFailure);
            } else if (iteration >= this.maxAttempts) {
                return Mono.error(this.backoffExceptionGenerator.apply(this, copy));
            } else {
                return Mono.delay(Duration.ofSeconds(delay.getSeconds() * (copy.totalRetries() + 1)))
                    .thenReturn(copy.totalRetries());
            }
        }).onErrorStop());
    }
}
