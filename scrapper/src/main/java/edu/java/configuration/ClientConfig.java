package edu.java.configuration;

import edu.java.client.bot.DefaultHttpBotClient;
import edu.java.client.bot.HttpBotClient;
import edu.java.client.github.GithubClient;
import edu.java.client.github.GithubWebClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.client.stackoverflow.StackOverflowWebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;
import static edu.java.utils.RetryUtils.getRetry;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ClientConfigurationProperties.class)
public class ClientConfig {

    private final ClientConfigurationProperties properties;

    @Bean
    public HttpBotClient httpBotClient(@Value("${client.bot.base-url}") String baseUrl) {
        Retry retryBackoff = getRetry(properties.bot().retryPolicy());
        return new DefaultHttpBotClient(baseUrl, retryBackoff);
    }

    @Bean
    public GithubClient githubClient(@Value("${client.github.base-url}") String baseUrl) {
        Retry retryBackoff = getRetry(properties.github().retryPolicy());
        return new GithubWebClient(baseUrl, retryBackoff);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(@Value("${client.stackoverflow.base-url}") String baseUrl) {
        Retry retryBackoff = getRetry(properties.stackOverflow().retryPolicy());
        return new StackOverflowWebClient(baseUrl, retryBackoff);
    }
}
