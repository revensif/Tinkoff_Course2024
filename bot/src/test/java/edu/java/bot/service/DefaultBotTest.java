package edu.java.bot.service;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Component
public class DefaultBotTest {

    @Autowired
    private LinkParser linkParser;

    @Autowired
    private LinkValidator linkValidator;

    @Autowired
    private MessageParser messageParser;

    @Test
    @DisplayName("Menu creation test")
    public void shouldCreateMenu() {
        //arrange
        UserMessageProcessor processor = mock(DefaultUserMessageProcessor.class);
        Command startCommand = new StartCommand(processor, linkParser, messageParser);
        List<? extends Command> commands = List.of(startCommand);
        Mockito.<List<? extends Command>>when(processor.commands()).thenReturn(commands);
        String token = System.getenv("TOKEN");
        DefaultBot bot = new DefaultBot(new ApplicationConfig(token, List.of()), processor);
        //act
        SetMyCommands result = bot.createMenu();
        //assert
        BotCommand[] botCommands = (BotCommand[]) result.getParameters().get("commands");
        assertThat(botCommands).isEqualTo(List.of(startCommand.toApiCommand()).toArray());
    }

    @Test
    @DisplayName("Process test")
    public void shouldProcessUpdates() {
        //arrange
        UserMessageProcessor processor = new DefaultUserMessageProcessor(linkParser, messageParser);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/start");
        when(message.chat()).thenReturn(chat);
        String token = System.getenv("TOKEN");
        DefaultBot bot = new DefaultBot(new ApplicationConfig(token, List.of()), processor);
        //act
        int process = bot.process(List.of(update));
        //assert
        assertThat(process).isEqualTo(-1);
    }
}
