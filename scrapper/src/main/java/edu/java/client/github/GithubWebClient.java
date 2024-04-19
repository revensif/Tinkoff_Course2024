package edu.java.client.github;

import edu.java.configuration.ClientConfigurationProperties;
import edu.java.dto.github.RepositoryResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import static edu.java.utils.RetryUtils.getRetry;

@Component
@EnableConfigurationProperties(ClientConfigurationProperties.class)
public class GithubWebClient implements GithubClient {

    private static final String REPOSITORY_ENDPOINT = "/repos/{owner}/{repo}";
    private final WebClient webClient;
    private final Retry retryBackoff;

    public GithubWebClient(ClientConfigurationProperties properties) {
        this.webClient = WebClient.builder()
            .baseUrl(properties.github().baseUrl())
            .build();
        this.retryBackoff = getRetry(properties.github().retryPolicy());
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
