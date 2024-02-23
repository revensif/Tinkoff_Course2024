package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.processor.UserMessageProcessor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ListCommand extends AbstractCommand {

    public static final String LIST_COMMAND = "/list";
    public static final String DESCRIPTION = "Command to show all tracked links";

    public ListCommand(UserMessageProcessor processor) {
        super(processor);
    }

    @Override
    public String command() {
        return LIST_COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        log.info("The user: {} requested the output of all tracked links", chatId);
        return new SendMessage(chatId, "It is not tracked because there is no database");
    }
}
