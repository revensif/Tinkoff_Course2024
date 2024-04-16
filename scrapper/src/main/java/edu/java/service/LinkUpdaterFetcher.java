package edu.java.service;

import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.QuestionRepository;
import edu.java.dto.Link;
import edu.java.service.checker.GithubUpdateChecker;
import edu.java.service.checker.StackOverflowUpdateChecker;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdaterFetcher {

    private final static List<UpdateChecker> UPDATE_CHECKERS = new ArrayList<>();

    public LinkUpdaterFetcher(
        @Qualifier("jdbcQuestionRepository") QuestionRepository questionRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient
    ) {
        UPDATE_CHECKERS.addAll(
            List.of(
                new GithubUpdateChecker(githubClient),
                new StackOverflowUpdateChecker(questionRepository, stackOverflowClient)
            )
        );
    }

    public UpdateChecker getUpdateChecker(Link link) {
        for (UpdateChecker checker : UPDATE_CHECKERS) {
            if (checker.supports(link)) {
                return checker;
            }
        }
        return null;
    }
}
