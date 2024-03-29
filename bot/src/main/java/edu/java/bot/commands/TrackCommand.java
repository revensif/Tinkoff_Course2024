package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.processor.UserMessageProcessor;
import edu.java.bot.utils.Link;
import java.net.URI;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import static edu.java.bot.utils.CommandMessageUtils.getURIFromMessage;
import static edu.java.bot.utils.Link.INCORRECT_LINK;
import static edu.java.bot.utils.LinkUtils.parse;

@Log4j2
public class TrackCommand extends AbstractCommand {

    public static final String TRACK_COMMAND = "/track";
    public static final String DESCRIPTION = "Command to track the link";

    public TrackCommand(UserMessageProcessor processor, HttpScrapperClient client) {
        super(processor, client);
    }

    @Override
    public String command() {
        return TRACK_COMMAND;
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
        log.info("The user: {} requested tracking of the link", chatId);
        String[] messageParts = message.text().split(" ");
        URI url = getURIFromMessage(messageParts);
        if (url == null) {
            return new SendMessage(chatId, INCORRECT_LINK);
        }
        Link link = parse(url);
        SendMessage sendMessage = new SendMessage(chatId, "The link is already being tracked");
        client.addLink(chatId, new AddLinkRequest(URI.create(link.toString())))
            .doOnNext(response -> handleClientResponse(response, chatId, sendMessage))
            .subscribe();
        return sendMessage;
    }

    private void handleClientResponse(LinkResponse response, long chatId, SendMessage sendMessage) {
        if (response.id() != null) {
            sendMessage = new SendMessage(chatId, "Now you are tracking this link");
        }
    }
}
