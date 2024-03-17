package edu.java.client.stackoverflow;

import edu.java.dto.Link;
import edu.java.dto.stackoverflow.CommentsResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.updates.UpdatesInfo;
import reactor.core.publisher.Mono;

public interface StackOverflowClient {
    Mono<QuestionResponse> fetchQuestion(Long id);

    Mono<CommentsResponse> fetchComments(Long id);

    UpdatesInfo getUpdatesInfo(Link link, Integer answerCount, Integer commentCount);
}
