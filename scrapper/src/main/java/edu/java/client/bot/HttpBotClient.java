package edu.java.client.bot;

import edu.java.dto.request.LinkUpdateRequest;
import reactor.core.publisher.Mono;

public interface HttpBotClient {

    Mono<String> sendUpdate(LinkUpdateRequest request);
}
