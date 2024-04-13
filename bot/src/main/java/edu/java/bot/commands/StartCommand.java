package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.commands.entities.Text;
import edu.java.bot.processor.UserMessageProcessor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class StartCommand extends AbstractCommand {

    private static final String START_COMMAND = "/start";
    private static final String DESCRIPTION = "Command to start the bot";

    public StartCommand(UserMessageProcessor processor, HttpScrapperClient client) {
        super(processor, client);
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
        Text text = new Text("The bot is already working");
        client.registerChat(chatId)
            .doOnNext(response -> handleClientResponse(response, text))
            .subscribe();
        return new SendMessage(chatId, text.getText());
    }

    private void handleClientResponse(String response, Text text) {
        if (response != null) {
            text.setText("Bot Started! Now you can track the available sites");
        }
    }
}
