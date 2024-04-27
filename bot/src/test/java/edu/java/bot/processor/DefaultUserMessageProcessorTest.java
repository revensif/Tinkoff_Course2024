package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DefaultUserMessageProcessorTest {

    @Autowired
    private UserMessageProcessor processor;

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
    }

    @Test
    @DisplayName("Commands method test")
    public void shouldReturnProcessorCommands() {
        assertThat(processor.commands().size()).isEqualTo(5);
        assertThat(processor.commands().stream()
            .anyMatch(command -> command.getClass() == StartCommand.class)).isTrue();
        assertThat(processor.commands().stream().anyMatch(command -> command.getClass() == ListCommand.class)).isTrue();
    }

    @Test
    @DisplayName("Process test : Correct command")
    public void shouldReturnCorrectResponse() {
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/list");
        SendMessage response = processor.process(update);
        assertThat(response.getParameters().get("text")).isEqualTo("You are not tracking any links");
    }

    @Test
    @DisplayName("Process test : Unknown command")
    public void shouldReturnIncorrectResponse() {
        when(message.text()).thenReturn("/something");
        SendMessage response = processor.process(update);
        assertThat(response.getParameters().get("text")).isEqualTo("Unknown command, try /help");
    }
}
