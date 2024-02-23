package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.commands.Command;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.processor.UserMessageProcessor;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class DefaultBot implements Bot {

    private final TelegramBot bot;
    private final UserMessageProcessor processor;

    public DefaultBot(ApplicationConfig config, UserMessageProcessor processor) {
        this.bot = new TelegramBot(config.telegramToken());
        this.processor = processor;
        this.bot.setUpdatesListener(this);
        this.bot.execute(createMenu());
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        bot.execute(request);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            if (update.message() != null) {
                SendMessage sendMessage = processor.process(update);
                bot.execute(sendMessage);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    public void start() {
        log.info("The bot is running");
    }

    @Override
    public void close() {
        log.info("The bot has been stopped");
        bot.shutdown();
    }

    public SetMyCommands createMenu() {
        BotCommand[] commands = processor.commands().stream()
            .map(Command::toApiCommand)
            .toArray(BotCommand[]::new);
        return new SetMyCommands(commands);
    }
}
