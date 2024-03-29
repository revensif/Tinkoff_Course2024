package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.processor.UserMessageProcessor;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ListCommand extends AbstractCommand {

    public static final String LIST_COMMAND = "/list";
    public static final String DESCRIPTION = "Command to show all tracked links";
    private static final String NO_LINKS_TRACKED = "You are not tracking any links";

    public ListCommand(UserMessageProcessor processor, HttpScrapperClient client) {
        super(processor, client);
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
        SendMessage message = new SendMessage(chatId, NO_LINKS_TRACKED);
        client.getAllLinks(chatId)
            .doOnNext(response -> handleClientResponse(response, chatId, message))
            .subscribe();
        return message;
    }

    private void handleClientResponse(ListLinksResponse response, long chatId, SendMessage message) {
        if (response.size() != 0) {
            List<LinkResponse> links = response.links();
            String commands = getAllLinks(links);
            message = new SendMessage(chatId, commands).disableWebPagePreview(true);
        }
    }

    private String getAllLinks(List<LinkResponse> links) {
        StringBuilder stringBuilder = new StringBuilder("List of tracked links:\n");
        for (LinkResponse link : links) {
            stringBuilder.append(" - ").append(link.url()).append("\n");
        }
        return String.valueOf(stringBuilder);
    }
}
