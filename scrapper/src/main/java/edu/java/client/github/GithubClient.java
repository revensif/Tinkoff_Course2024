package edu.java.client.github;

import edu.java.dto.github.RepositoryResponse;
import reactor.core.publisher.Mono;

public interface GithubClient {
    Mono<RepositoryResponse> fetchRepository(String owner, String repo);
}
