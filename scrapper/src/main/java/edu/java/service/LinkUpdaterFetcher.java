package edu.java.service;

import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.QuestionRepository;
import edu.java.dto.Link;
import edu.java.service.checker.GithubUpdateChecker;
import edu.java.service.checker.StackOverflowUpdateChecker;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdaterFetcher {

    private final List<UpdateChecker> updateCheckers;

    public LinkUpdaterFetcher(List<UpdateChecker> updateCheckers) {
        this.updateCheckers = updateCheckers;
    }

    public UpdateChecker getUpdateChecker(Link link) {
        for (UpdateChecker checker : updateCheckers) {
            if (checker.supports(link)) {
                return checker;
            }
        }
        return null;
    }
}
