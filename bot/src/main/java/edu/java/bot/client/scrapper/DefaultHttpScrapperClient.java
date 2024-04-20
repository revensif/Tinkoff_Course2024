package edu.java.bot.client.scrapper;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class DefaultHttpScrapperClient implements HttpScrapperClient {

    private static final String HEADER = "Tg-Chat-Id";
    private static final String LINKS_ENDPOINT = "/links";
    private static final String TG_CHAT_ENDPOINT = "/tg-chat/{id}";

    private final WebClient webClient;

    public DefaultHttpScrapperClient(
        @Value("${scrapper.base-url}") String baseUrl,
        ExchangeFilterFunction filterFunction
    ) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .filter(filterFunction)
            .build();
    }

    @Override
    public Mono<String> registerChat(Long id) {
        return webClient.post()
            .uri(TG_CHAT_ENDPOINT, id)
            .retrieve()
            .bodyToMono(String.class);
    }

    @Override
    public Mono<String> deleteChat(Long id) {
        return webClient.delete()
            .uri(TG_CHAT_ENDPOINT, id)
            .retrieve()
            .bodyToMono(String.class);
    }

    @Override
    public Mono<ListLinksResponse> getAllLinks(Long id) {
        return webClient.get()
            .uri(LINKS_ENDPOINT)
            .header(HEADER, String.valueOf(id))
            .retrieve()
            .bodyToMono(ListLinksResponse.class);
    }

    @Override
    public Mono<LinkResponse> addLink(Long id, AddLinkRequest request) {
        return webClient.post()
            .uri(LINKS_ENDPOINT)
            .header(HEADER, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }

    @Override
    public Mono<LinkResponse> deleteLink(Long id, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(LINKS_ENDPOINT)
            .header(HEADER, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }
}
