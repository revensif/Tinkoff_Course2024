package edu.java.configuration;

import edu.java.client.bot.HttpBotClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jpa.JpaChatLinkRepository;
import edu.java.dao.repository.jpa.JpaChatRepository;
import edu.java.dao.repository.jpa.JpaLinkRepository;
import edu.java.dao.repository.jpa.JpaQuestionRepository;
import edu.java.dao.repository.jpa.inner_repository.InnerJpaChatLinkRepository;
import edu.java.dao.repository.jpa.inner_repository.InnerJpaChatRepository;
import edu.java.dao.repository.jpa.inner_repository.InnerJpaLinkRepository;
import edu.java.dao.repository.jpa.inner_repository.InnerJpaQuestionRepository;
import edu.java.service.LinkUpdater;
import edu.java.service.LinkUpdaterFetcher;
import edu.java.service.LinksService;
import edu.java.service.TgChatService;
import edu.java.service.jpa.JpaLinkUpdater;
import edu.java.service.jpa.JpaLinksService;
import edu.java.service.jpa.JpaTgChatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
@EnableJpaRepositories(basePackages = "edu.java.dao.repository.jpa.inner_repository")
public class JpaConfig {

    @Bean
    public LinksService jpaLinksService(
        StackOverflowClient client,
        JpaQuestionRepository questionRepository,
        JpaLinkRepository linkRepository,
        JpaChatLinkRepository chatLinkRepository,
        @Value("#{@resources}") List<String> resources
    ) {
        return new JpaLinksService(client, questionRepository, linkRepository, chatLinkRepository, resources);
    }

    @Bean
    public TgChatService jpaTgChatService(JpaChatRepository chatRepository) {
        return new JpaTgChatService(chatRepository);
    }

    @Bean
    public LinkUpdater jpaLinkUpdater(
        JpaLinkRepository linkRepository,
        JpaChatLinkRepository chatLinkRepository,
        HttpBotClient httpBotClient,
        LinkUpdaterFetcher linkUpdaterFetcher
    ) {
        return new JpaLinkUpdater(
            linkRepository,
            chatLinkRepository,
            httpBotClient,
            linkUpdaterFetcher
        );
    }

    @Bean
    public JpaChatRepository jpaChatRepository(InnerJpaChatRepository innerJpaChatRepository) {
        return new JpaChatRepository(innerJpaChatRepository);
    }

    @Bean
    public JpaLinkRepository jpaLinkRepository(InnerJpaLinkRepository innerJpaLinkRepository) {
        return new JpaLinkRepository(innerJpaLinkRepository);
    }

    @Bean
    public JpaChatLinkRepository jpaChatLinkRepository(
        InnerJpaChatLinkRepository innerJpaChatLinkRepository,
        InnerJpaLinkRepository innerJpaLinkRepository
    ) {
        return new JpaChatLinkRepository(innerJpaChatLinkRepository, innerJpaLinkRepository);
    }

    @Bean JpaQuestionRepository jpaQuestionRepository(InnerJpaQuestionRepository innerJpaQuestionRepository) {
        return new JpaQuestionRepository(innerJpaQuestionRepository);
    }
}
