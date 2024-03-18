package edu.java.client.stackoverflow;

import edu.java.dto.Link;
import edu.java.dto.stackoverflow.CommentsResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.updates.UpdatesInfo;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackOverflowWebClient implements StackOverflowClient {

    private static final String BASE_URL = "https://api.stackexchange.com/2.3/";
    private static final String QUESTION_ENDPOINT = "/questions/{id}?site=stackoverflow";
    private static final String COMMENTS_ENDPOINT = "/questions/{id}/comments?site=stackoverflow";
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
    public Mono<CommentsResponse> fetchComments(Long id) {
        return webClient.get()
            .uri(COMMENTS_ENDPOINT, id)
            .retrieve()
            .bodyToMono(CommentsResponse.class);
    }

    @Override
    public UpdatesInfo getUpdatesInfo(Link link, Integer answerCount, Integer commentCount) {
        String path = link.getUrl().getPath();
        String[] pathParts = path.split("/");
        Long id = Long.parseLong(pathParts[pathParts.length - 2]);
        QuestionResponse questionResponse = fetchQuestion(id).block();
        CommentsResponse commentsResponse = fetchComments(id).block();
        var question = questionResponse.items().getFirst();
        if (question.lastActivityDate().isAfter(link.getUpdatedAt())) {
            if (question.answerCount() > answerCount) {
                return new UpdatesInfo(true, question.lastActivityDate(), "There is a new answer!");
            }
            if (commentsResponse.items().size() > commentCount) {
                return new UpdatesInfo(true, question.lastActivityDate(), "There is a new comment!");
            }
            return new UpdatesInfo(true, question.lastActivityDate(), "The question has been updated!");
        }
        return new UpdatesInfo(false, question.lastActivityDate(), "There are no updates!");
    }
}
