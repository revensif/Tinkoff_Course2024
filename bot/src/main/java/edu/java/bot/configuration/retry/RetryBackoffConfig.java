package edu.java.bot.configuration.retry;

import edu.java.bot.retry.backoff.ConstantRetryBackoff;
import edu.java.bot.retry.backoff.ExponentialRetryBackoff;
import edu.java.bot.retry.backoff.LinearRetryBackoff;
import edu.java.bot.retry.backoff.RetryBackoff;
import edu.java.bot.retry.policy.RetryPolicy;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RetryBackoffConfigurationProperties.class)
public class RetryBackoffConfig {

    private final RetryBackoffConfigurationProperties configProperties;

    @Bean
    public RetryPolicy retryPolicy(RetryBackoff retryBackoff) {
        return new RetryPolicy(
            configProperties.statuses().stream().map(HttpStatusCode::valueOf).toList(),
            retryBackoff
        );
    }

    @Bean
    @ConditionalOnProperty(prefix = "retry", name = "backoff-type", havingValue = "constant")
    public RetryBackoff constantRetryBackoff() {
        return new ConstantRetryBackoff(configProperties.baseDelay());
    }

    @Bean
    @ConditionalOnProperty(prefix = "retry", name = "backoff-type", havingValue = "exponential")
    public RetryBackoff exponentialRetryBackoff() {
        return new ExponentialRetryBackoff(
            configProperties.baseDelay(),
            configProperties.maxDelay(),
            configProperties.multiplier()
        );
    }

    @Bean
    @ConditionalOnProperty(prefix = "retry", name = "backoff-type", havingValue = "linear")
    public RetryBackoff linearRetryBackoff() {
        return new LinearRetryBackoff(
            configProperties.baseDelay(),
            configProperties.maxDelay(),
            configProperties.increment()
        );
    }

    @Bean
    public ExchangeFilterFunction retryExchangeFilterFunction(RetryPolicy retryPolicy) {
        return new RetryExchangeFilterFunction(configProperties.maxAttempts(), retryPolicy);
    }

    @RequiredArgsConstructor
    public static class RetryExchangeFilterFunction implements ExchangeFilterFunction {

        private final int maxAttempts;
        private final RetryPolicy retryPolicy;

        @Override
        public @NotNull Mono<ClientResponse> filter(@NotNull ClientRequest request, @NotNull ExchangeFunction next) {
            return recursiveRetry(request, next, 1);
        }

        private Mono<ClientResponse> recursiveRetry(ClientRequest request, ExchangeFunction next, int attempt) {
            return next.exchange(request)
                .flatMap(clientResponse -> {
                    if ((retryPolicy.statuses().contains(clientResponse.statusCode())) && (attempt < maxAttempts)) {
                        Duration calculatedRetryDuration = retryPolicy.retryBackoff().calculateRetryDuration(attempt);
                        return Mono.delay(calculatedRetryDuration)
                            .then(recursiveRetry(request, next, attempt + 1));
                    }
                    return Mono.just(clientResponse);
                });
        }
    }
}
