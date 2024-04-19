package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserMessageProcessor implements UserMessageProcessor {

    private final List<Command> commands;

    public DefaultUserMessageProcessor(@Lazy List<Command> commands) {
        this.commands = commands;
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
