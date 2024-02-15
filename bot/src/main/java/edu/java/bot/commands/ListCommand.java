package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.processor.UserMessageProcessor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ListCommand extends AbstractCommand {

    public ListCommand(UserMessageProcessor processor) {
        super(processor);
    }

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "Command to show all tracked links";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        return new SendMessage(chatId, "It is not tracked because there is no database");
    }
}
