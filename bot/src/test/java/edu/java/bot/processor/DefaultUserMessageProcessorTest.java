package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.dto.response.ListLinksResponse;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import org.junit.Before;
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
public class DefaultUserMessageProcessorTest {

    private final MeterRegistry registry = new SimpleMeterRegistry();
    private final HttpScrapperClient client = mock(HttpScrapperClient.class);

    @Autowired
    private List<Command> commands;

    private Update update;
    private Message message;

    @Before
    public void setup() {
        update = mock(Update.class);
        message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(10L);
        Mono<ListLinksResponse> linksResponse = Mono.just(
            new ListLinksResponse(null, 0)
        );
        when(client.getAllLinks(any(Long.class))).thenReturn(linksResponse);
    }

    @Test
    @DisplayName("Commands method test")
    public void shouldReturnProcessorCommands() {
        UserMessageProcessor processor = new DefaultUserMessageProcessor(commands, registry);
        assertThat(processor.commands().size()).isEqualTo(5);
        assertThat(processor.commands().stream()
            .anyMatch(command -> command.getClass() == StartCommand.class)).isTrue();
        assertThat(processor.commands().stream().anyMatch(command -> command.getClass() == ListCommand.class)).isTrue();
    }

    @Test
    @DisplayName("Process test : Correct command")
    public void shouldReturnCorrectResponse() {
        //arrange
        UserMessageProcessor processor = new DefaultUserMessageProcessor(commands, registry);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/list");
        //act
        SendMessage response = processor.process(update);
        double messagesProcessedNumber = registry.counter("messages_processed_number").count();
        //assert
        assertThat(response.getParameters().get("text")).isEqualTo("You are not tracking any links");
        assertThat(messagesProcessedNumber).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Metrics counter test")
    public void shouldIncrementProcessedCommands() {
        //arrange
        UserMessageProcessor processor = new DefaultUserMessageProcessor(commands, registry);
        List<String> commands = List.of("/help", "/something", "/list", "/list", "/start");
        when(client.registerChat(any(Long.class))).thenReturn(Mono.just("OK"));
        for (String command : commands) {
            when(message.text()).thenReturn(command);
            //act
            processor.process(update);
        }
        double messagesProcessedNumber = registry.counter("messages_processed_number").count();
        double startCommandsNumber =
            registry.counter("commands_processed_number", "command_type", "/start").count();
        double helpCommandsNumber =
            registry.counter("commands_processed_number", "command_type", "/help").count();
        double listCommandsNumber =
            registry.counter("commands_processed_number", "command_type", "/list").count();
        //assert
        assertThat(messagesProcessedNumber).isEqualTo(commands.size());
        assertThat(startCommandsNumber).isEqualTo(1.0);
        assertThat(helpCommandsNumber).isEqualTo(1.0);
        assertThat(listCommandsNumber).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Process test : Unknown command")
    public void shouldReturnIncorrectResponse() {
        UserMessageProcessor processor = new DefaultUserMessageProcessor(commands, registry);
        when(message.text()).thenReturn("/something");
        SendMessage response = processor.process(update);
        assertThat(response.getParameters().get("text")).isEqualTo("Unknown command, try /help");
    }
}
