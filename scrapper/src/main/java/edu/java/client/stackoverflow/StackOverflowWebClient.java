package edu.java.client.stackoverflow;

import edu.java.dto.Link;
import edu.java.dto.stackoverflow.QuestionResponse;
import java.time.OffsetDateTime;
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

    @Override
    public OffsetDateTime getUpdatedAt(Link link) {
        String path = link.getUrl().getPath();
        String[] pathParts = path.split("/");
        QuestionResponse response = fetchQuestion(Long.parseLong(pathParts[pathParts.length - 2])).block();
        return response.items().getFirst().lastActivityDate();
    }
}
