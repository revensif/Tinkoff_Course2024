package edu.java.bot.configuration;

import edu.java.bot.client.scrapper.DefaultHttpScrapperClient;
import edu.java.bot.client.scrapper.HttpScrapperClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;
import static edu.java.bot.utils.RetryUtils.getRetry;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ClientConfigurationProperties.class)
public class ClientConfig {

    private final ClientConfigurationProperties properties;

    @Bean
    public HttpScrapperClient defaultHttpScrapperClient(@Value("${client.scrapper.base-url}") String baseUrl) {
        Retry retryBackoff = getRetry(properties.scrapper().retryPolicy());
        return new DefaultHttpScrapperClient(baseUrl, retryBackoff);
    }
}
