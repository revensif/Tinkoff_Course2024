package edu.java.bot.commands;

import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.processor.UserMessageProcessor;

public abstract class AbstractCommand implements Command {

    protected final UserMessageProcessor processor;
    protected final HttpScrapperClient client;

    public AbstractCommand(UserMessageProcessor processor, HttpScrapperClient client) {
        this.processor = processor;
        this.client = client;
    }

    @Override
    public String toString() {
        return command() + " - " + description();
    }
}
