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
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JdbcLinkUpdater implements LinkUpdater {

    private final JdbcQuestionRepository questionRepository;
    private final JdbcLinkRepository linkRepository;
    private final JdbcChatLinkRepository chatLinkRepository;
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;
    private final HttpBotClient httpBotClient;
    private static final Duration THRESHOLD = Duration.ofDays(10);
    private final List<String> resources;

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
            if (link.url().getHost().matches(resources.getFirst())) {
                fetchGithubRepository(pathParts, updatesInfo);
            } else if (link.url().getHost().matches(resources.getLast())) {
                fetchStackoverflowQuestionAndComment(pathParts, updatesInfo, linkId);
            }
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
        Question questionInDatabase = questionRepository.findByLinkId(linkId);
        int requestedAnswerCount = requestedQuestion.answerCount();
        int requestedCommentCount = requestedQuestion.commentCount();
        int databaseAnswerCount = questionInDatabase.answerCount();
        int databaseCommentCount = questionInDatabase.commentCount();
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
