package edu.java.bot;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Bean
    public DefaultBot defaultBot(ApplicationConfig config, UserMessageProcessor processor) {
        return new DefaultBot(config, processor);
    }

    @Bean
    public UserMessageProcessor defaultUserMessageProcessor() {
        return new DefaultUserMessageProcessor();
    }
}
