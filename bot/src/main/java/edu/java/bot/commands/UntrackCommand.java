package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.processor.UserMessageProcessor;
import edu.java.bot.utils.Link;
import java.net.URI;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import static edu.java.bot.utils.CommandMessageUtils.getURIFromMessage;
import static edu.java.bot.utils.Link.INCORRECT_LINK;
import static edu.java.bot.utils.LinkUtils.parse;

@Log4j2
public class UntrackCommand extends AbstractCommand {

    public static final String UNTRACK_COMMAND = "/untrack";
    public static final String DESCRIPTION = "Command to stop tracking the link";

    public UntrackCommand(UserMessageProcessor processor) {
        super(processor);
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
        URI url = getURIFromMessage(messageParts);
        if (url == null) {
            return new SendMessage(chatId, INCORRECT_LINK);
        }
        Link link = parse(url);
        return new SendMessage(chatId, "The link " + link + " is no longer being tracked");
    }
}
