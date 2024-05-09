package edu.java.configuration;

import edu.java.utils.RetryPolicy;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientConfigurationProperties(@NotNull Bot bot, @NotNull Github github,
                                            @NotNull StackOverflow stackOverflow) {

    public record Bot(String baseUrl, RetryPolicy retryPolicy) {
    }

    public record Github(String baseUrl, RetryPolicy retryPolicy) {
    }

    public record StackOverflow(String baseUrl, RetryPolicy retryPolicy) {
    }
}
