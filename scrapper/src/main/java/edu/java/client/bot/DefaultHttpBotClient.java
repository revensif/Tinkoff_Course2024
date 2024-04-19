package edu.java.client.bot;

import edu.java.configuration.ClientConfigurationProperties;
import edu.java.dto.request.LinkUpdateRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import static edu.java.utils.RetryUtils.getRetry;

@Component
@EnableConfigurationProperties(ClientConfigurationProperties.class)
public class DefaultHttpBotClient implements HttpBotClient {

    private static final String UPDATES_ENDPOINT = "/updates";
    private final WebClient webClient;
    private final Retry retryBackoff;

    public DefaultHttpBotClient(ClientConfigurationProperties properties) {
        this.webClient = WebClient.builder()
            .baseUrl(properties.bot().baseUrl())
            .build();
        this.retryBackoff = getRetry(properties.bot().retryPolicy());
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
