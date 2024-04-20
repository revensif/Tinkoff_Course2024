package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserMessageProcessor implements UserMessageProcessor {

    private final Counter messageCounter;
    private final MeterRegistry registry;
    private final List<Command> commands;

    public DefaultUserMessageProcessor(@Lazy List<Command> commands, MeterRegistry registry) {
        this.commands = commands;
        this.messageCounter = registry.counter("messages_processed_number");
        this.registry = registry;
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
        messageCounter.increment();
        if (commandOptional.isPresent()) {
            Command command = commandOptional.get();
            registry.counter("commands_processed_number",
                "command_type", command.command()
            ).increment();
            return command.handle(update);
        } else {
            return new SendMessage(update.message().chat().id(), "Unknown command, try /help");
        }
    }
}
