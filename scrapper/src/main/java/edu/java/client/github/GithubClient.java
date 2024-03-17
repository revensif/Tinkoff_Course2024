package edu.java.client.github;

import edu.java.dto.Link;
import edu.java.dto.github.RepositoryResponse;
import edu.java.updates.UpdatesInfo;
import reactor.core.publisher.Mono;

public interface GithubClient {
    Mono<RepositoryResponse> fetchRepository(String owner, String repo);

    UpdatesInfo getUpdatesInfo(Link link);
}
