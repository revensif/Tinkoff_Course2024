package edu.java.client.bot;

import edu.java.configuration.ClientConfigurationProperties;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.retry.RetryBuilder;
import edu.java.utils.RetryPolicy;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@EnableConfigurationProperties(ClientConfigurationProperties.class)
public class DefaultHttpBotClient implements HttpBotClient {

    private static final String UPDATES_ENDPOINT = "/updates";
    private final WebClient webClient;
    private final Retry retryBackoff;

    public DefaultHttpBotClient(ClientConfigurationProperties properties, Map<String, RetryBuilder> retryBuilderMap) {
        this.webClient = WebClient.builder()
            .baseUrl(properties.bot().baseUrl())
            .build();
        RetryPolicy retryPolicy = properties.bot().retryPolicy();
        RetryBuilder builder = retryBuilderMap.get(retryPolicy.backoffType());
        this.retryBackoff = builder == null ? Retry.max(0)
            : builder.build(retryPolicy.maxAttempts(), retryPolicy.delay(), retryPolicy.statuses());
    }

    @Override
    public Mono<String> sendUpdate(LinkUpdateRequest request) {
        return webClient.post()
            .uri(UPDATES_ENDPOINT)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(retryBackoff);
    }
}
