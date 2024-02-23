package edu.java.bot.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public interface Command {

    String command();

    String description();

    SendMessage handle(Update update);

    default boolean supports(Update update) {
        return ((update != null)
            && (update.message() != null)
            && (update.message().text() != null)
            && (checkCommand(update)));
    }

    default BotCommand toApiCommand() {
        return new BotCommand(command(), description());
    }

    //В будущем можно будет добавить дополнительные условия
    private boolean checkCommand(Update update) {
        return update.message().text().split(" ")[0].equals(command());
    }
}
