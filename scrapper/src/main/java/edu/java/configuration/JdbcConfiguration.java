package edu.java.configuration;

import edu.java.client.bot.HttpBotClient;
import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcChatRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.service.jdbc.JdbcLinkUpdater;
import edu.java.service.jdbc.JdbcLinksService;
import edu.java.service.jdbc.JdbcTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcConfiguration {

    @Bean
    public JdbcLinksService jdbcLinksService(
        JdbcLinkRepository linkRepository,
        JdbcChatLinkRepository chatLinkRepository
    ) {
        return new JdbcLinksService(linkRepository, chatLinkRepository);
    }

    @Bean
    public JdbcTgChatService jdbcTgChatService(JdbcChatRepository chatRepository) {
        return new JdbcTgChatService(chatRepository);
    }

    @Bean
    public JdbcLinkUpdater jdbcLinkUpdater(
        JdbcLinkRepository linkRepository,
        JdbcChatLinkRepository chatLinkRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        HttpBotClient httpBotClient
    ) {
        return new JdbcLinkUpdater(
            linkRepository,
            chatLinkRepository,
            githubClient,
            stackOverflowClient,
            httpBotClient
        );
    }
}
