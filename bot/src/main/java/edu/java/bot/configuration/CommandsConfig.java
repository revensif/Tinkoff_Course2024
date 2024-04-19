package edu.java.bot.configuration;

import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.service.LinkParser;
import edu.java.bot.service.MessageParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandsConfig {

    @Bean
    public Command helpCommand(
        DefaultUserMessageProcessor processor,
        HttpScrapperClient client,
        LinkParser linkParser,
        MessageParser messageParser
    ) {
        return new HelpCommand(processor, client, linkParser, messageParser);
    }

    @Bean
    public Command listCommand(
        DefaultUserMessageProcessor processor,
        HttpScrapperClient client,
        LinkParser linkParser,
        MessageParser messageParser
    ) {
        return new ListCommand(processor, client, linkParser, messageParser);
    }

    @Bean
    public Command startCommand(
        DefaultUserMessageProcessor processor,
        HttpScrapperClient client,
        LinkParser linkParser,
        MessageParser messageParser
    ) {
        return new StartCommand(processor, client, linkParser, messageParser);
    }

    @Bean
    public Command trackCommand(
        DefaultUserMessageProcessor processor,
        HttpScrapperClient client,
        LinkParser linkParser,
        MessageParser messageParser
    ) {
        return new TrackCommand(processor, client, linkParser, messageParser);
    }

    @Bean
    public Command untrackCommand(
        DefaultUserMessageProcessor processor,
        HttpScrapperClient client,
        LinkParser linkParser,
        MessageParser messageParser
    ) {
        return new UntrackCommand(processor, client, linkParser, messageParser);
    }
}
