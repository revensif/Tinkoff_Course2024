package edu.java.client.github;

import edu.java.dto.github.RepositoryResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class GithubWebClient implements GithubClient {

    private static final String BASE_URL = "https://localhost:8080";
    private static final String REPOSITORY_ENDPOINT = "/repos/{owner}/{repo}";
    private final WebClient webClient;
    private final Retry retryBackoff;

    public GithubWebClient(Retry retryBackoff) {
        this(BASE_URL, retryBackoff);
    }

    public GithubWebClient(String baseUrl, Retry retryBackoff) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
        this.retryBackoff = retryBackoff;
    }

    @Override
    public Mono<RepositoryResponse> fetchRepository(String owner, String repo) {
        return webClient.get()
            .uri(REPOSITORY_ENDPOINT, owner, repo)
            .retrieve()
            .bodyToMono(RepositoryResponse.class)
            .retryWhen(retryBackoff);
    }
}
