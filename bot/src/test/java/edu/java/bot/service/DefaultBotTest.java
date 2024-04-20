package edu.java.bot.service;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.commands.Command;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DefaultBotTest {

    @Autowired
    private Command startCommand;

    @Autowired
    private List<Command> commands;

    private final MeterRegistry registry = new SimpleMeterRegistry();
    private final HttpScrapperClient client = mock(HttpScrapperClient.class);

    @Test
    @DisplayName("Menu creation test")
    public void shouldCreateMenu() {
        //arrange
        UserMessageProcessor processor = new DefaultUserMessageProcessor(List.of(startCommand), registry);
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
        UserMessageProcessor processor = new DefaultUserMessageProcessor(commands, registry);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/start");
        when(message.chat()).thenReturn(chat);
        when(client.registerChat(any(Long.class))).thenReturn(Mono.just("Ok"));
        String token = System.getenv("TOKEN");
        DefaultBot bot =
            new DefaultBot(new ApplicationConfig(token, List.of(), new ApplicationConfig.ScrapperTopic("")), processor);
        //act
        int process = bot.process(List.of(update));
        double messagesProcessedNumber = registry.counter("messages_processed_number").count();
        //assert
        assertThat(process).isEqualTo(-1);
        assertThat(messagesProcessedNumber).isEqualTo(1.0);
    }
}
