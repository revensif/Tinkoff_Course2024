package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.commands.entities.Commands;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.processor.UserMessageProcessor;
import edu.java.bot.service.LinkParser;
import edu.java.bot.service.MessageParser;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ListCommand extends AbstractCommand {

    public static final String LIST_COMMAND = "/list";
    public static final String DESCRIPTION = "Command to show all tracked links";
    private static final String NO_LINKS_TRACKED = "You are not tracking any links";

    public ListCommand(
        UserMessageProcessor processor,
        HttpScrapperClient client,
        LinkParser linkParser,
        MessageParser messageParser
    ) {
        super(processor, client, linkParser, messageParser);
    }

    @Override
    public String command() {
        return LIST_COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        log.info("The user: {} requested the output of all tracked links", chatId);
        Commands commands = new Commands(NO_LINKS_TRACKED);
        client.getAllLinks(chatId)
            .doOnNext(response -> handleClientResponse(response, commands))
            .subscribe();
        return new SendMessage(chatId, commands.getCommands()).disableWebPagePreview(true);
    }

    private void handleClientResponse(ListLinksResponse response, Commands commands) {
        if (response.size() != 0) {
            List<LinkResponse> links = response.links();
            commands.setCommands(getAllLinks(links));
        }
    }

    private String getAllLinks(List<LinkResponse> links) {
        StringBuilder stringBuilder = new StringBuilder("List of tracked links:\n");
        for (LinkResponse link : links) {
            stringBuilder.append("- ").append(link.url()).append("\n");
        }
        return String.valueOf(stringBuilder);
    }
}
