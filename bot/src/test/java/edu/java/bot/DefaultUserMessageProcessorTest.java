import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultUserMessageProcessorTest {

    @Test
    @DisplayName("Commands method test")
    public void shouldReturnProcessorCommands() {
        UserMessageProcessor processor = new DefaultUserMessageProcessor();
        assertThat(processor.commands().size()).isEqualTo(5);
        assertThat(processor.commands().getFirst()).isInstanceOf(StartCommand.class);
        assertThat(processor.commands().getLast()).isInstanceOf(ListCommand.class);
    }

    @Test
    @DisplayName("Process test : Correct command")
    public void shouldReturnCorrectResponse() {
        UserMessageProcessor processor = new DefaultUserMessageProcessor();
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/list");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(10L);
        SendMessage response = processor.process(update);
        assertThat(response.getParameters().get("text")).isEqualTo("It is not tracked because there is no database");
    }

    @Test
    @DisplayName("Process test : Unknown command")
    public void shouldReturnIncorrectResponse() {
        UserMessageProcessor processor = new DefaultUserMessageProcessor();
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/something");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(10L);
        SendMessage response = processor.process(update);
        assertThat(response.getParameters().get("text")).isEqualTo("Unknown command, try /help");
    }
}
