package edu.java.client.stackoverflow;

import edu.java.dto.Link;
import edu.java.dto.stackoverflow.QuestionResponse;
import java.time.OffsetDateTime;
import reactor.core.publisher.Mono;

public interface StackOverflowClient {
    Mono<QuestionResponse> fetchQuestion(Long id);

    OffsetDateTime getUpdatedAt(Link link);
}
