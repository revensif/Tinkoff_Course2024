package commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListCommandTest {

    private final UserMessageProcessor processor = new DefaultUserMessageProcessor();

    @Test
    @DisplayName("Command name and description Test")
    public void shouldReturnNameAndDescription() {
        Command listCommand = new ListCommand(processor);
        assertThat(listCommand.command()).isEqualTo("/list");
        assertThat(listCommand.description()).isEqualTo("Command to show all tracked links");
    }

    @Test
    @DisplayName("All tracked links test")
    public void shouldReturnCorrectResponse() {
        Command listCommand = new ListCommand(processor);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/list");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(10L);
        SendMessage response = listCommand.handle(update);
        assertThat(response.getParameters().get("text")).isEqualTo("It is not tracked because there is no database");
    }
}
