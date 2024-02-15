package edu.java.bot.commands;

import edu.java.bot.processor.UserMessageProcessor;

public abstract class AbstractCommand implements Command {

    protected final UserMessageProcessor processor;

    public AbstractCommand(UserMessageProcessor processor) {
        this.processor = processor;
    }

    @Override public String toString() {
        return command() + " - " + description();
    }
}
