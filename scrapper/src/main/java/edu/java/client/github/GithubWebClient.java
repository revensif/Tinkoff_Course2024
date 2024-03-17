package edu.java.client.github;

import edu.java.dto.Link;
import edu.java.dto.github.RepositoryResponse;
import edu.java.updates.UpdatesInfo;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GithubWebClient implements GithubClient {

    private static final String BASE_URL = "https://api.github.com/";
    private static final String REPOSITORY_ENDPOINT = "/repos/{owner}/{repo}";
    private final WebClient webClient;

    public GithubWebClient() {
        this(BASE_URL);
    }

    public GithubWebClient(String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    @Override
    public Mono<RepositoryResponse> fetchRepository(String owner, String repo) {
        return webClient.get()
            .uri(REPOSITORY_ENDPOINT, owner, repo)
            .retrieve()
            .bodyToMono(RepositoryResponse.class);
    }

    @Override
    public UpdatesInfo getUpdatesInfo(Link link) {
        String path = link.getUrl().getPath();
        String[] pathParts = path.split("/");
        RepositoryResponse response = fetchRepository(pathParts[1], pathParts[2]).block();
        if (response.updatedAt().isAfter(link.getUpdatedAt())) {
            return new UpdatesInfo(true, response.updatedAt(), "The repository has been updated!");
        } else {
            return new UpdatesInfo(false, response.updatedAt(), "There are no updates!");
        }
    }
}
