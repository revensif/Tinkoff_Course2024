package edu.java.configuration;

import edu.java.client.bot.HttpBotClient;
import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcChatRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dao.repository.jdbc.JdbcQuestionRepository;
import edu.java.service.LinkUpdater;
import edu.java.service.LinksService;
import edu.java.service.TgChatService;
import edu.java.service.jdbc.JdbcLinkUpdater;
import edu.java.service.jdbc.JdbcLinksService;
import edu.java.service.jdbc.JdbcTgChatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcConfig {

    @Bean
    public LinksService jdbcLinksService(
        StackOverflowClient client,
        JdbcQuestionRepository questionRepository,
        JdbcLinkRepository linkRepository,
        JdbcChatLinkRepository chatLinkRepository,
        @Value("#{@resources}") List<String> resources
    ) {
        return new JdbcLinksService(client, questionRepository, linkRepository, chatLinkRepository, resources);
    }

    @Bean
    public TgChatService jdbcTgChatService(JdbcChatRepository chatRepository) {
        return new JdbcTgChatService(chatRepository);
    }

    @Bean
    public LinkUpdater jdbcLinkUpdater(
        JdbcQuestionRepository questionRepository,
        JdbcLinkRepository linkRepository,
        JdbcChatLinkRepository chatLinkRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        HttpBotClient httpBotClient,
        @Value("#{@resources}") List<String> resources
    ) {
        return new JdbcLinkUpdater(
            questionRepository,
            linkRepository,
            chatLinkRepository,
            githubClient,
            stackOverflowClient,
            httpBotClient,
            resources
        );
    }
}
