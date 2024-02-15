package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.processor.UserMessageProcessor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class HelpCommand extends AbstractCommand {

    public HelpCommand(UserMessageProcessor processor) {
        super(processor);
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return "Command to display a window with commands";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        StringBuilder sb = new StringBuilder("List of commands:\n");
        log.info("The user requested the output of a window with all the commands");
        for (Command command : processor.commands()) {
            sb.append(command).append("\n");
        }
        return new SendMessage(chatId, String.valueOf(sb));
    }
}
