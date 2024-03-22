package edu.java.configuration;

import edu.java.client.bot.HttpBotClient;
import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jpa.JpaChatRepository;
import edu.java.dao.repository.jpa.JpaLinkRepository;
import edu.java.dao.repository.jpa.JpaQuestionRepository;
import edu.java.service.LinkUpdater;
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

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaConfig {

    @Bean
    public LinksService jpaLinksService(
        StackOverflowClient client,
        JpaQuestionRepository questionRepository,
        JpaLinkRepository linkRepository,
        JpaChatRepository chatRepository,
        @Value("#{@resources}") List<String> resources
    ) {
        return new JpaLinksService(client, questionRepository, linkRepository, chatRepository, resources);
    }

    @Bean
    public TgChatService jpaTgChatService(JpaChatRepository chatRepository) {
        return new JpaTgChatService(chatRepository);
    }

    @Bean
    public LinkUpdater jpaLinkUpdater(
        JpaQuestionRepository questionRepository,
        JpaLinkRepository linkRepository,
        JpaChatRepository chatRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        HttpBotClient httpBotClient,
        @Value("#{@resources}") List<String> resources
    ) {
        return new JpaLinkUpdater(
            questionRepository,
            linkRepository,
            chatRepository,
            githubClient,
            stackOverflowClient,
            httpBotClient,
            resources
        );
    }
}
