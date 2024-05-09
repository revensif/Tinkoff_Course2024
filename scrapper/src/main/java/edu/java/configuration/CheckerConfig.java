package edu.java.configuration;

import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.QuestionRepository;
import edu.java.service.UpdateChecker;
import edu.java.service.checker.GithubUpdateChecker;
import edu.java.service.checker.StackOverflowUpdateChecker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CheckerConfig {

    @Bean
    public UpdateChecker githubUpdateChecker(GithubClient githubClient) {
        return new GithubUpdateChecker(githubClient);
    }

    @Bean
    public UpdateChecker stackOverflowUpdateChecker(
        @Qualifier("jdbcQuestionRepository") QuestionRepository questionRepository, StackOverflowClient client
    ) {
        return new StackOverflowUpdateChecker(questionRepository, client);
    }
}
