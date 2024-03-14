package edu.java.service.jdbc;

import edu.java.client.bot.HttpBotClient;
import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dto.Link;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.service.LinkUpdater;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
        int updatesCount = 0;
        List<Link> outdatedLinks = linkRepository.findOutdatedLinks(THRESHOLD);
        for (Link link : outdatedLinks) {
            OffsetDateTime updatedAt = link.getUpdatedAt();
            if (link.getUrl().getHost().matches(GITHUB)) {
                updatedAt = githubClient.getUpdatedAt(link);
            } else if (link.getUrl().getHost().matches(STACKOVERFLOW)) {
                updatedAt = stackOverflowClient.getUpdatedAt(link);
            }
            if (updatedAt.isAfter(link.getUpdatedAt())) {
                httpBotClient.sendUpdate(new LinkUpdateRequest(
                    link.getLinkId(),
                    link.getUrl(),
                    "The link has been updated",
                    chatLinkRepository.findAllChatsThatTrackThisLink(link.getLinkId())
                ));
                updatesCount++;
            }
            linkRepository.changeUpdatedAt(link.getUrl(), updatedAt);
        }
        return updatesCount;
    }
}
