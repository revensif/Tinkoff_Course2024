package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.processor.UserMessageProcessor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class StartCommand extends AbstractCommand {

    private static final String START_COMMAND = "/start";
    private static final String DESCRIPTION = "Command to start the bot";

    public StartCommand(UserMessageProcessor processor) {
        super(processor);
    }

    @Override
    public String command() {
        return START_COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        log.info("A new user has been added: {}", chatId);
        return new SendMessage(chatId, "Bot Started! Now you can track the available sites.");
    }
}
