package edu.java.client.stackoverflow;

import edu.java.dto.stackoverflow.QuestionResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackOverflowWebClient implements StackOverflowClient {

    private static final String BASE_URL = "https://api.stackexchange.com/2.3/";
    private static final String QUESTION_ENDPOINT = "/questions/{id}?site=stackoverflow";
    private final WebClient webClient;

    public StackOverflowWebClient() {
        this(BASE_URL);
    }

    public StackOverflowWebClient(String baseUrl) {
        webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    @Override
    public Mono<QuestionResponse> fetchQuestion(Long id) {
        return webClient.get()
            .uri(QUESTION_ENDPOINT, id)
            .retrieve()
            .bodyToMono(QuestionResponse.class);
    }
}
