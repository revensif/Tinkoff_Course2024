package edu.java.client.stackoverflow;

import edu.java.dto.stackoverflow.CommentsResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackOverflowWebClient implements StackOverflowClient {

    private static final String BASE_URL = "https://api.stackexchange.com/2.3/";
    private static final String QUESTION_ENDPOINT = "/questions/{id}?site=stackoverflow";
    private static final String COMMENTS_ENDPOINT = "/questions/{id}/comments?site=stackoverflow";
    private final WebClient webClient;

    public StackOverflowWebClient(ExchangeFilterFunction filterFunction) {
        this(BASE_URL, filterFunction);
    }

    public StackOverflowWebClient(String baseUrl, ExchangeFilterFunction filterFunction) {
        webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .filter(filterFunction)
            .build();
    }

    @Override
    public Mono<QuestionResponse> fetchQuestion(Long id) {
        return webClient.get()
            .uri(QUESTION_ENDPOINT, id)
            .retrieve()
            .bodyToMono(QuestionResponse.class);
    }

    @Override
    public Mono<CommentsResponse> fetchComments(Long id) {
        return webClient.get()
            .uri(COMMENTS_ENDPOINT, id)
            .retrieve()
            .bodyToMono(CommentsResponse.class);
    }
}
