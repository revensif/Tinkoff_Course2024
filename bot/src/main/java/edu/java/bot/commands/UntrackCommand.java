package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.commands.entities.Text;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.processor.UserMessageProcessor;
import edu.java.bot.service.LinkParser;
import edu.java.bot.service.MessageParser;
import edu.java.bot.utils.Link;
import java.net.URI;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import static edu.java.bot.utils.Link.INCORRECT_LINK;

@Log4j2
@Component
public class UntrackCommand extends AbstractCommand {

    public static final String UNTRACK_COMMAND = "/untrack";
    public static final String DESCRIPTION = "Command to stop tracking the link";

    public UntrackCommand(
        UserMessageProcessor processor,
        LinkParser linkParser,
        MessageParser messageParser,
        HttpScrapperClient client
    ) {
        super(processor, linkParser, messageParser, client);
    }

    @Override
    public String command() {
        return UNTRACK_COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @SneakyThrows
    @Override
    public SendMessage handle(Update update) {
        var message = update.message();
        long chatId = message.chat().id();
        log.info("The user: {} requested to stop tracking the link", chatId);
        String[] messageParts = message.text().split(" ");
        URI url = messageParser.parseMessage(messageParts);
        if (url == null) {
            return new SendMessage(chatId, INCORRECT_LINK);
        }
        Link link = linkParser.parseLink(url);
        Text text = new Text("This link is already not tracked");
        client.deleteLink(chatId, new RemoveLinkRequest(URI.create(link.toString())))
            .doOnNext(response -> handleClientResponse(response, text))
            .subscribe();
        return new SendMessage(chatId, text.getText());
    }

    private void handleClientResponse(LinkResponse response, Text text) {
        if (response.id() != null) {
            text.setText("The link is no longer being tracked");
        }
    }
}
