package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import io.micrometer.core.instrument.Counter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserMessageProcessor implements UserMessageProcessor {

    private final List<Command> commands;
    private final Map<String, Counter> counters;

    public DefaultUserMessageProcessor(
        @Lazy List<Command> commands,
        Map<String, Counter> counters
    ) {
        this.commands = commands;
        this.counters = counters;
    }

    @Override
    public List<? extends Command> commands() {
        return commands;
    }

    @Override
    public SendMessage process(Update update) {
        Optional<Command> commandOptional = commands.stream()
            .filter((com) -> com.supports(update))
            .findFirst();
        counters.get("messageCounter").increment();
        if (commandOptional.isPresent()) {
            Command command = commandOptional.get();
            counters.get(command.command()).increment();
            return command.handle(update);
        } else {
            return new SendMessage(update.message().chat().id(), "Unknown command, try /help");
        }
    }
}
