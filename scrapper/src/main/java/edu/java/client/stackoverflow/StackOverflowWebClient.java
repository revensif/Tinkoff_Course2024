package edu.java.client.stackoverflow;

import edu.java.configuration.ClientConfigurationProperties;
import edu.java.dto.stackoverflow.CommentsResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.retry.RetryBuilder;
import edu.java.utils.RetryPolicy;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@EnableConfigurationProperties(ClientConfigurationProperties.class)
public class StackOverflowWebClient implements StackOverflowClient {

    private static final String QUESTION_ENDPOINT = "/questions/{id}?site=stackoverflow";
    private static final String COMMENTS_ENDPOINT = "/questions/{id}/comments?site=stackoverflow";
    private final WebClient webClient;
    private final Retry retryBackoff;

    public StackOverflowWebClient(ClientConfigurationProperties properties, Map<String, RetryBuilder> retryBuilderMap) {
        this.webClient = WebClient.builder()
            .baseUrl(properties.stackOverflow().baseUrl())
            .build();
        RetryPolicy retryPolicy = properties.stackOverflow().retryPolicy();
        RetryBuilder builder = retryBuilderMap.get(retryPolicy.backoffType());
        this.retryBackoff = builder == null ? Retry.max(0)
            : builder.build(retryPolicy.maxAttempts(), retryPolicy.delay(), retryPolicy.statuses());
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
