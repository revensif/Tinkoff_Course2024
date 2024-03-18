package edu.java.service.jdbc;

import edu.java.client.bot.HttpBotClient;
import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dao.repository.jdbc.JdbcQuestionRepository;
import edu.java.dto.Link;
import edu.java.dto.Question;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.service.LinkUpdater;
import edu.java.updates.UpdatesInfo;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.utils.LinkUtils.GITHUB;
import static edu.java.utils.LinkUtils.STACKOVERFLOW;

@Service
@RequiredArgsConstructor
public class JdbcLinkUpdater implements LinkUpdater {

    private final JdbcQuestionRepository questionRepository;
    private final JdbcLinkRepository linkRepository;
    private final JdbcChatLinkRepository chatLinkRepository;
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;
    private final HttpBotClient httpBotClient;
    private static final Duration THRESHOLD = Duration.ofDays(10);

    @Override
    @Transactional
    public int update() {
        int updatesCount = 0;
        List<Link> outdatedLinks = linkRepository.findOutdatedLinks(THRESHOLD);
        for (Link link : outdatedLinks) {
            UpdatesInfo updatesInfo = new UpdatesInfo(false, link.getUpdatedAt(), "There are no updates!");
            if (link.getUrl().getHost().matches(GITHUB)) {
                updatesInfo = githubClient.getUpdatesInfo(link);
            } else if (link.getUrl().getHost().matches(STACKOVERFLOW)) {
                Question question = questionRepository.findByLinkId(link.getLinkId());
                updatesInfo =
                    stackOverflowClient.getUpdatesInfo(link, question.getAnswerCount(), question.getCommentCount());
            }
            if (updatesInfo.updatedAt().isAfter(link.getUpdatedAt())) {
                httpBotClient.sendUpdate(new LinkUpdateRequest(
                    link.getLinkId(),
                    link.getUrl(),
                    updatesInfo.message(),
                    chatLinkRepository.findAllChatsThatTrackThisLink(link.getLinkId())
                ));
                updatesCount++;
            }
            linkRepository.changeUpdatedAt(link.getUrl(), updatesInfo.updatedAt());
        }
        return updatesCount;
    }
}
