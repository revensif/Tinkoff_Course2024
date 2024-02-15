package commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HelpCommandTest {

    private final UserMessageProcessor processor = new DefaultUserMessageProcessor();

    @Test
    @DisplayName("Command name and description Test")
    public void shouldReturnNameAndDescription() {
        Command helpCommand = new HelpCommand(processor);
        assertThat(helpCommand.command()).isEqualTo("/help");
        assertThat(helpCommand.description()).isEqualTo("Command to display a window with commands");
    }

    @Test
    @DisplayName("All commands list test")
    public void shouldReturnCorrectResponse() {
        Command helpCommand = new HelpCommand(processor);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/help");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(10L);
        SendMessage response = helpCommand.handle(update);
        StringBuilder sb = new StringBuilder("List of commands:\n");
        for (Command command : processor.commands()) {
            sb.append(command).append("\n");
        }
        assertThat(response.getParameters().get("text")).isEqualTo(String.valueOf(sb));
    }
}
