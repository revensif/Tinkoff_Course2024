package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.processor.UserMessageProcessor;
import edu.java.bot.utils.Link;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.log4j.Log4j2;
import static edu.java.bot.utils.Link.INCORRECT_LINK;
import static edu.java.bot.utils.LinkUtils.isCorrectLink;
import static edu.java.bot.utils.LinkUtils.parse;

@Log4j2
public class UntrackCommand extends AbstractCommand {

    public UntrackCommand(UserMessageProcessor processor) {
        super(processor);
    }

    @Override
    public String command() {
        return "/untrack <link>";
    }

    @Override
    public String description() {
        return "Command to stop tracking the link";
    }

    @Override
    public SendMessage handle(Update update) {
        var message = update.message();
        long chatId = message.chat().id();
        String[] messageParts = message.text().split(" ");
        if (messageParts.length != 2) {
            return new SendMessage(chatId, INCORRECT_LINK);
        }
        try {
            log.info("The user requested to stop tracking the link");
            URI url = new URI(messageParts[1]);
            if (!isCorrectLink(url)) {
                return new SendMessage(chatId, INCORRECT_LINK);
            }
            Link link = parse(url);
            return new SendMessage(chatId, "The link " + link + " is no longer being tracked");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
