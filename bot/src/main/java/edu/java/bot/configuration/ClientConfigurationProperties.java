package edu.java.bot.configuration;

import edu.java.bot.utils.RetryPolicy;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientConfigurationProperties(@NotNull Scrapper scrapper) {

    protected record Scrapper(String baseUrl, RetryPolicy retryPolicy) {
    }
}
