package edu.java.client.bot;

import edu.java.dto.request.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class DefaultHttpBotClient implements HttpBotClient {

    private static final String UPDATES_ENDPOINT = "/updates";
    private final WebClient webClient;

    public DefaultHttpBotClient(@Value("${bot.base-url}") String baseUrl) {
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
