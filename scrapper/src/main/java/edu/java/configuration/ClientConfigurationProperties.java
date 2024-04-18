package edu.java.configuration;

import edu.java.utils.RetryPolicy;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientConfigurationProperties(@NotNull Bot bot, @NotNull Github github,
                                            @NotNull StackOverflow stackOverflow) {

    protected record Bot(String baseUrl, RetryPolicy retryPolicy) {
    }

    protected record Github(String baseUrl, RetryPolicy retryPolicy) {
    }

    protected record StackOverflow(String baseUrl, RetryPolicy retryPolicy) {
    }
}
