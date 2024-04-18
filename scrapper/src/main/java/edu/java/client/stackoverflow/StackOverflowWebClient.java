package edu.java.client.stackoverflow;

import edu.java.dto.stackoverflow.CommentsResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class StackOverflowWebClient implements StackOverflowClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String QUESTION_ENDPOINT = "/questions/{id}?site=stackoverflow";
    private static final String COMMENTS_ENDPOINT = "/questions/{id}/comments?site=stackoverflow";
    private final WebClient webClient;
    private final Retry retryBackoff;

    public StackOverflowWebClient(Retry retryBackoff) {
        this(BASE_URL, retryBackoff);
    }

    public StackOverflowWebClient(String baseUrl, Retry retryBackoff) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
        this.retryBackoff = retryBackoff;
    }

    @Override
    public Mono<QuestionResponse> fetchQuestion(Long id) {
        return webClient.get()
            .uri(QUESTION_ENDPOINT, id)
            .retrieve()
            .bodyToMono(QuestionResponse.class)
            .retryWhen(retryBackoff);
    }

    @Override
    public Mono<CommentsResponse> fetchComments(Long id) {
        return webClient.get()
            .uri(COMMENTS_ENDPOINT, id)
            .retrieve()
            .bodyToMono(CommentsResponse.class)
            .retryWhen(retryBackoff);
    }
}
