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
public class StartCommand extends AbstractCommand {

    private static final String START_COMMAND = "/start";
    private static final String DESCRIPTION = "Command to start the bot";

    public StartCommand(
        UserMessageProcessor processor,
        HttpScrapperClient client,
        LinkParser linkParser,
        MessageParser messageParser
    ) {
        super(processor, client, linkParser, messageParser);
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
        SendMessage message = new SendMessage(chatId, "The bot is already working");
        client.registerChat(chatId)
            .doOnNext(response -> handleClientResponse(response, message, chatId))
            .subscribe();
        return message;
    }

    private void handleClientResponse(String response, SendMessage message, long chatId) {
        if (response != null) {
            message = new SendMessage(chatId, "Bot Started! Now you can track the available sites");
        }
    }
}
