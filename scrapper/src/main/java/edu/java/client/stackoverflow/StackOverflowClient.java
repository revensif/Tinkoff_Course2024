package edu.java.client.stackoverflow;

import edu.java.dto.stackoverflow.CommentsResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import reactor.core.publisher.Mono;

public interface StackOverflowClient {
    Mono<QuestionResponse> fetchQuestion(Long id);

    Mono<CommentsResponse> fetchComments(Long id);
}
