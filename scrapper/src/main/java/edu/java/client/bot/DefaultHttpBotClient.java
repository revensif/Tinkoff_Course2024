package edu.java.client.bot;

import edu.java.dto.request.LinkUpdateRequest;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class DefaultHttpBotClient implements HttpBotClient {

    private static final String BASE_URL = "https://localhost:8090";
    private static final String UPDATES_ENDPOINT = "/updates";
    private final WebClient webClient;

    public DefaultHttpBotClient() {
        this(BASE_URL);
    }

    public DefaultHttpBotClient(String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    @Override
    public Mono<String> sendUpdate(LinkUpdateRequest request) {
        return webClient.post()
            .uri(UPDATES_ENDPOINT)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(String.class);
    }
}
