package edu.java.service.checker;

import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.QuestionRepository;
import edu.java.dto.Link;
import edu.java.dto.Question;
import edu.java.service.UpdateChecker;
import edu.java.updates.UpdatesInfo;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class StackOverflowUpdateChecker implements UpdateChecker {

    private final QuestionRepository questionRepository;
    private final StackOverflowClient stackOverflowClient;
    private static final String STACKOVERFLOW = "stackoverflow.com";

    @Override
    public boolean supports(Link link) {
        return link.url().getHost().matches(STACKOVERFLOW);
    }

    @Override
    public void checkForUpdates(UpdatesInfo updatesInfo, String[] pathParts, long linkId) {
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
