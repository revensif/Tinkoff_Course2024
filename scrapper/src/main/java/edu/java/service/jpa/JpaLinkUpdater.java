package edu.java.service.jpa;

import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jpa.JpaChatRepository;
import edu.java.dao.repository.jpa.JpaLinkRepository;
import edu.java.dao.repository.jpa.JpaQuestionRepository;
import edu.java.dto.Link;
import edu.java.dto.Question;
import edu.java.dto.entity.ChatEntity;
import edu.java.dto.entity.QuestionEntity;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.service.LinkUpdater;
import edu.java.service.notification.GeneralNotificationService;
import edu.java.updates.UpdatesInfo;
import edu.java.utils.EntityUtils;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JpaLinkUpdater implements LinkUpdater {

    private final JpaQuestionRepository questionRepository;
    private final JpaLinkRepository linkRepository;
    private final JpaChatRepository chatRepository;
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;
    private final GeneralNotificationService notificationService;
    private static final Duration DURATION = Duration.ofDays(10);
    private static final OffsetDateTime THRESHOLD = OffsetDateTime.now().minus(DURATION);
    private final List<String> resources;

    @Override
    @Transactional
    public int update() {
        AtomicInteger updatesCount = new AtomicInteger(0);
        List<Link> outdatedLinks = linkRepository.findOutdatedLinks(THRESHOLD)
            .stream()
            .map(EntityUtils::linkEntityToLink)
            .toList();
        for (Link link : outdatedLinks) {
            long linkId = link.linkId();
            String path = link.url().getPath();
            String[] pathParts = path.split("/");
            UpdatesInfo updatesInfo = new UpdatesInfo(false, link.updatedAt(), "There are no updates!");
            if (link.url().getHost().matches(resources.getFirst())) {
                fetchGithubRepository(pathParts, updatesInfo);
            } else if (link.url().getHost().matches(resources.getLast())) {
                fetchStackoverflowQuestionAndComment(pathParts, updatesInfo, linkId);
            }
            if (updatesInfo.getUpdatedAt().isAfter(link.updatedAt())) {
                notificationService.sendUpdate(new LinkUpdateRequest(
                    linkId,
                    link.url(),
                    updatesInfo.getMessage(),
                    chatRepository.findAllByLinksLinkId(linkId)
                        .stream()
                        .map(ChatEntity::getChatId)
                        .toList()
                ));
                updatesCount.addAndGet(1);
                linkRepository.changeUpdatedAt(link.url().toString(), updatesInfo.getUpdatedAt());
            }
        }
        return updatesCount.get();
    }

    private void fetchGithubRepository(String[] pathParts, UpdatesInfo updatesInfo) {
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

    private void fetchStackoverflowQuestionAndComment(String[] pathParts, UpdatesInfo updatesInfo, long linkId) {
        long questionId = Long.parseLong(pathParts[pathParts.length - 2]);
        Mono.zip(stackOverflowClient.fetchQuestion(questionId), stackOverflowClient.fetchComments(questionId))
            .doOnNext(response -> updatesInfo.setSomethingUpdated(response.getT1().items().getFirst()
                .lastActivityDate().isAfter(updatesInfo.getUpdatedAt())))
            .filter(response -> updatesInfo.isSomethingUpdated())
            .doOnNext(response -> updatesInfo.setUpdatedAt(response.getT1().items().getFirst()
                .lastActivityDate()))
            .map(response -> new Question(
                linkId,
                response.getT1().items().getFirst().answerCount(),
                response.getT2().items().size()
            ))
            .doOnNext(question -> changeUpdatesInfoForStackOverflow(linkId, question, updatesInfo))
            .subscribe();
    }

    private void changeUpdatesInfoForStackOverflow(long linkId, Question requestedQuestion, UpdatesInfo updatesInfo) {
        QuestionEntity questionInDatabase = questionRepository.findById(linkId).get();
        int requestedAnswerCount = requestedQuestion.answerCount();
        int requestedCommentCount = requestedQuestion.commentCount();
        int databaseAnswerCount = questionInDatabase.getAnswerCount();
        int databaseCommentCount = questionInDatabase.getAnswerCount();
        if ((requestedAnswerCount > databaseAnswerCount) && (requestedCommentCount > databaseCommentCount)) {
            questionRepository.changeAnswerCount(linkId, requestedAnswerCount);
            questionRepository.changeCommentCount(linkId, requestedCommentCount);
            updatesInfo.setMessage("There is a new answer and comment!");
        } else if (requestedAnswerCount > databaseAnswerCount) {
            questionRepository.changeAnswerCount(linkId, requestedAnswerCount);
            updatesInfo.setMessage("There is a new answer!");
        } else if (requestedCommentCount > databaseCommentCount) {
            questionRepository.changeCommentCount(linkId, requestedCommentCount);
            updatesInfo.setMessage("There is a new comment!");
        } else {
            updatesInfo.setMessage("The question has been updated!");
        }
    }
}
