package edu.java.bot.service;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.Command;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import java.util.List;
import java.util.Map;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DefaultBotTest {

    @Autowired
    private Command startCommand;

    @Autowired
    private Map<String, Counter> counters;

    @Test
    @DisplayName("Menu creation test")
    public void shouldCreateMenu() {
        //arrange
        UserMessageProcessor processor = mock(DefaultUserMessageProcessor.class);
        List<? extends Command> commands = List.of(startCommand);
        Mockito.<List<? extends Command>>when(processor.commands()).thenReturn(commands);
        String token = System.getenv("TOKEN");
        DefaultBot bot =
            new DefaultBot(new ApplicationConfig(token, List.of(), new ApplicationConfig.ScrapperTopic("")), processor);
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
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        UserMessageProcessor processor = new DefaultUserMessageProcessor(List.of(startCommand), counters);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/start");
        when(message.chat()).thenReturn(chat);
        String token = System.getenv("TOKEN");
        DefaultBot bot =
            new DefaultBot(new ApplicationConfig(token, List.of(), new ApplicationConfig.ScrapperTopic("")), processor);
        //act
        int process = bot.process(List.of(update));
        double messagesProcessedNumber = counters.get("messageCounter").count();
        double startCommandsNumber = counters.get(startCommand.command()).count();
        //assert
        assertThat(process).isEqualTo(-1);
        assertThat(messagesProcessedNumber).isEqualTo(1.0);
        assertThat(startCommandsNumber).isEqualTo(1.0);
    }
}
