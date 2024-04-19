package edu.java.service.checker;

import edu.java.client.github.GithubClient;
import edu.java.dto.Link;
import edu.java.service.UpdateChecker;
import edu.java.updates.UpdatesInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GithubUpdateChecker implements UpdateChecker {

    private final GithubClient githubClient;
    private static final String GITHUB = "github.com";

    @Override
    public boolean supports(Link link) {
        return link.url().getHost().matches(GITHUB);
    }

    @Override
    public void checkForUpdates(UpdatesInfo updatesInfo, String[] pathParts, long linkId) {
        githubClient.fetchRepository(pathParts[1], pathParts[2])
            .doOnNext(response -> updatesInfo.setSomethingUpdated(response.updatedAt()
                .isAfter(updatesInfo.getUpdatedAt())))
            .filter(response -> updatesInfo.isSomethingUpdated())
            .doOnNext(response -> {
                updatesInfo.setUpdatedAt(response.updatedAt());
                updatesInfo.setMessage("The repository has been updated!");
            })
            .subscribe();
    }
}
