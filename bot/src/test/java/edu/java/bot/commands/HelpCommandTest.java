package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HelpCommandTest {

    private final UserMessageProcessor processor = new DefaultUserMessageProcessor();
    private final Command helpCommand = new HelpCommand(processor);

    @Test
    @DisplayName("Command name and description Test")
    public void shouldReturnNameAndDescription() {
        assertThat(helpCommand.command()).isEqualTo("/help");
        assertThat(helpCommand.description()).isEqualTo("Command to display a window with commands");
    }

    @Test
    @DisplayName("All commands list test")
    public void shouldReturnCorrectResponse() {
        //arrange
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/help");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(10L);
        //act
        SendMessage response = helpCommand.handle(update);
        //assert
        String expected = getAllCommands();
        assertThat(response.getParameters().get("text")).isEqualTo(expected);
    }

    private String getAllCommands() {
        StringBuilder sb = new StringBuilder("List of commands:\n");
        for (Command command : processor.commands()) {
            sb.append(command).append("\n");
        }
        return String.valueOf(sb);
    }
}
