package edu.java.service.jdbc;

import edu.java.client.bot.HttpBotClient;
import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dto.Link;
import edu.java.dto.github.RepositoryResponse;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.service.LinkUpdater;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcLinkUpdater implements LinkUpdater {

    private final JdbcLinkRepository linkRepository;
    private final JdbcChatLinkRepository chatLinkRepository;
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;
    private final HttpBotClient httpBotClient;
    private static final String GITHUB = "github\\.com";
    private static final String STACKOVERFLOW = "stackoverflow\\.com";
    private static final Duration THRESHOLD = Duration.ofDays(10);

    @Override
    @Transactional
    public int update() {
        AtomicInteger updatesCount = new AtomicInteger(0);
        List<Link> outdatedLinks = linkRepository.findOutdatedLinks(THRESHOLD);
        for (Link link : outdatedLinks) {
            String path = link.url().getPath();
            String[] pathParts = path.split("/");
            if (link.url().getHost().matches(GITHUB)) {
                githubClient.fetchRepository(pathParts[1], pathParts[2])
                    .map(RepositoryResponse::updatedAt)
                    .filter(response -> response.isAfter(link.updatedAt()))
                    .doOnNext(response -> sendUpdate(link, updatesCount))
                    .doOnNext(response -> linkRepository.changeUpdatedAt(link.url(), response))
                    .subscribe();
            } else if (link.url().getHost().matches(STACKOVERFLOW)) {
                stackOverflowClient.fetchQuestion(Long.parseLong(pathParts[pathParts.length - 2]))
                    .map(QuestionResponse::items)
                    .map(itemResponses -> itemResponses.getFirst().lastActivityDate())
                    .filter(response -> response.isAfter(link.updatedAt()))
                    .doOnNext(response -> sendUpdate(link, updatesCount))
                    .doOnNext(response -> linkRepository.changeUpdatedAt(link.url(), response))
                    .subscribe();
            }
        }
        return updatesCount.get();
    }

    private void sendUpdate(Link link, AtomicInteger updatesCount) {
        httpBotClient.sendUpdate(new LinkUpdateRequest(
            link.linkId(),
            link.url(),
            "The link has been updated",
            chatLinkRepository.findAllChatsThatTrackThisLink(link.linkId())
        ));
        updatesCount.addAndGet(1);
    }
}
