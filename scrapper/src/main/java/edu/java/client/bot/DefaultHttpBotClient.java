package edu.java.client.bot;

import edu.java.dto.request.LinkUpdateRequest;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class DefaultHttpBotClient implements HttpBotClient {

    private static final String UPDATES_ENDPOINT = "/updates";
    private final WebClient webClient;
    private final Retry retryBackoff;

    public DefaultHttpBotClient(String baseUrl, Retry retryBackoff) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
        this.retryBackoff = retryBackoff;
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
