package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.processor.UserMessageProcessor;
import edu.java.bot.service.LinkParser;
import edu.java.bot.service.MessageParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class HelpCommand extends AbstractCommand {

    public static final String HELP_COMMAND = "/help";
    public static final String DESCRIPTION = "Command to display a window with commands";

    public HelpCommand(
        UserMessageProcessor processor,
        LinkParser linkParser,
        MessageParser messageParser,
        HttpScrapperClient client
    ) {
        super(processor, linkParser, messageParser, client);
    }

    @Override
    public String command() {
        return HELP_COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        log.info("The user: {} requested the output of a window with all the commands", chatId);
        String commands = getAllCommands();
        return new SendMessage(chatId, commands);
    }

    private String getAllCommands() {
        StringBuilder sb = new StringBuilder("List of commands:\n");
        for (Command command : processor.commands()) {
            sb.append(command).append("\n");
        }
        return String.valueOf(sb);
    }
}
