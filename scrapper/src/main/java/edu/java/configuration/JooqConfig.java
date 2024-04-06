package edu.java.configuration;

import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jooq.JooqChatLinkRepository;
import edu.java.dao.repository.jooq.JooqChatRepository;
import edu.java.dao.repository.jooq.JooqLinkRepository;
import edu.java.dao.repository.jooq.JooqQuestionRepository;
import edu.java.service.LinkUpdater;
import edu.java.service.LinksService;
import edu.java.service.TgChatService;
import edu.java.service.jooq.JooqLinkUpdater;
import edu.java.service.jooq.JooqLinksService;
import edu.java.service.jooq.JooqTgChatService;
import edu.java.service.notification.GeneralNotificationService;
import java.util.List;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqConfig {

    @Bean
    public DefaultConfigurationCustomizer postgresJooqCustomizer() {
        return (DefaultConfiguration configuration) -> configuration.settings()
            .withRenderFormatted(true)
            .withRenderQuotedNames(RenderQuotedNames.NEVER);
    }

    @Bean
    public LinksService jooqdbcLinksService(
        StackOverflowClient client,
        JooqQuestionRepository questionRepository,
        JooqLinkRepository linkRepository,
        JooqChatLinkRepository chatLinkRepository,
        @Value("#{@resources}") List<String> resources
    ) {
        return new JooqLinksService(client, questionRepository, linkRepository, chatLinkRepository, resources);
    }

    @Bean
    public TgChatService jooqTgChatService(JooqChatRepository chatRepository) {
        return new JooqTgChatService(chatRepository);
    }

    @Bean
    public LinkUpdater jooqLinkUpdater(
        JooqQuestionRepository questionRepository,
        JooqLinkRepository linkRepository,
        JooqChatLinkRepository chatLinkRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        GeneralNotificationService notificationService,
        @Value("#{@resources}") List<String> resources
    ) {
        return new JooqLinkUpdater(
            questionRepository,
            linkRepository,
            chatLinkRepository,
            githubClient,
            stackOverflowClient,
            notificationService,
            resources
        );
    }
}
