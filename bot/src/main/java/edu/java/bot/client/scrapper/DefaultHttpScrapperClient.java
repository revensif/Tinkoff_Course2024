package edu.java.bot.client.scrapper;

import edu.java.bot.configuration.ClientConfigurationProperties;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.retry.RetryBuilder;
import edu.java.bot.utils.RetryPolicy;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@EnableConfigurationProperties(ClientConfigurationProperties.class)
public class DefaultHttpScrapperClient implements HttpScrapperClient {

    private static final String HEADER = "Tg-Chat-Id";
    private static final String LINKS_ENDPOINT = "/links";
    private static final String TG_CHAT_ENDPOINT = "/tg-chat/{id}";
    private final WebClient webClient;
    private final Retry retryBackoff;

    public DefaultHttpScrapperClient(
        ClientConfigurationProperties properties,
        Map<String, RetryBuilder> retryBuilderMap
    ) {
        this.webClient = WebClient.builder()
            .baseUrl(properties.scrapper().baseUrl())
            .build();
        RetryPolicy retryPolicy = properties.scrapper().retryPolicy();
        RetryBuilder builder = retryBuilderMap.get(retryPolicy.backoffType());
        this.retryBackoff = builder == null ? Retry.max(0)
            : builder.build(retryPolicy.maxAttempts(), retryPolicy.delay(), retryPolicy.statuses());
    }

    @Override
    public Mono<String> registerChat(Long id) {
        return webClient.post()
            .uri(TG_CHAT_ENDPOINT, id)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(retryBackoff);
    }

    @Override
    public Mono<String> deleteChat(Long id) {
        return webClient.delete()
            .uri(TG_CHAT_ENDPOINT, id)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(retryBackoff);
    }

    @Override
    public Mono<ListLinksResponse> getAllLinks(Long id) {
        return webClient.get()
            .uri(LINKS_ENDPOINT)
            .header(HEADER, String.valueOf(id))
            .retrieve()
            .bodyToMono(ListLinksResponse.class)
            .retryWhen(retryBackoff);
    }

    @Override
    public Mono<LinkResponse> addLink(Long id, AddLinkRequest request) {
        return webClient.post()
            .uri(LINKS_ENDPOINT)
            .header(HEADER, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(LinkResponse.class)
            .retryWhen(retryBackoff);
    }

    @Override
    public Mono<LinkResponse> deleteLink(Long id, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(LINKS_ENDPOINT)
            .header(HEADER, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(LinkResponse.class)
            .retryWhen(retryBackoff);
    }
}
