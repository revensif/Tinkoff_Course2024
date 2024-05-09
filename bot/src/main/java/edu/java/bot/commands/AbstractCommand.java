package edu.java.bot.commands;

import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.processor.UserMessageProcessor;
import edu.java.bot.service.LinkParser;
import edu.java.bot.service.MessageParser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractCommand implements Command {

    protected final UserMessageProcessor processor;
    protected final HttpScrapperClient client;
    protected final LinkParser linkParser;
    protected final MessageParser messageParser;

    @Override
    public String toString() {
        return command() + " - " + description();
    }
}
