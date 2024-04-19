package edu.java.service.jooq;

import edu.java.client.bot.HttpBotClient;
import edu.java.dao.repository.jooq.JooqChatLinkRepository;
import edu.java.dao.repository.jooq.JooqLinkRepository;
import edu.java.dto.Link;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.service.LinkUpdater;
import edu.java.service.LinkUpdaterFetcher;
import edu.java.service.UpdateChecker;
import edu.java.updates.UpdatesInfo;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JooqLinkUpdater implements LinkUpdater {

    private final JooqLinkRepository linkRepository;
    private final JooqChatLinkRepository chatLinkRepository;
    private final HttpBotClient httpBotClient;
    private final LinkUpdaterFetcher linkUpdaterFetcher;
    private static final Duration THRESHOLD = Duration.ofDays(10);

    @Override
    @Transactional
    public int update() {
        AtomicInteger updatesCount = new AtomicInteger(0);
        List<Link> outdatedLinks = linkRepository.findOutdatedLinks(THRESHOLD);
        for (Link link : outdatedLinks) {
            long linkId = link.linkId();
            String path = link.url().getPath();
            String[] pathParts = path.split("/");
            UpdatesInfo updatesInfo = new UpdatesInfo(false, link.updatedAt(), "There are no updates!");
            UpdateChecker checker = linkUpdaterFetcher.getUpdateChecker(link);
            if (checker == null) {
                continue;
            }
            checker.checkForUpdates(updatesInfo, pathParts, linkId);
            if (updatesInfo.getUpdatedAt().isAfter(link.updatedAt())) {
                httpBotClient.sendUpdate(new LinkUpdateRequest(
                    linkId,
                    link.url(),
                    updatesInfo.getMessage(),
                    chatLinkRepository.findAllChatsThatTrackThisLink(link.linkId())
                ));
                updatesCount.addAndGet(1);
                linkRepository.changeUpdatedAt(link.url(), updatesInfo.getUpdatedAt());
            }
        }
        return updatesCount.get();
    }
}
