package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.service.LinkParser;
import edu.java.bot.service.MessageParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserMessageProcessor implements UserMessageProcessor {

    private final List<Command> commands = new ArrayList<>();

    public DefaultUserMessageProcessor(LinkParser linkParser, MessageParser messageParser) {
        this.commands.addAll(List.of(
            new StartCommand(this, linkParser, messageParser),
            new HelpCommand(this, linkParser, messageParser),
            new TrackCommand(this, linkParser, messageParser),
            new UntrackCommand(this, linkParser, messageParser),
            new ListCommand(this, linkParser, messageParser)
        ));
    }

    @Override
    public List<? extends Command> commands() {
        return commands;
    }

    @Override
    public SendMessage process(Update update) {
        Optional<Command> command = commands.stream()
            .filter((com) -> com.supports(update))
            .findFirst();
        if (command.isPresent()) {
            return command.get().handle(update);
        } else {
            return new SendMessage(update.message().chat().id(), "Unknown command, try /help");
        }
    }
}
