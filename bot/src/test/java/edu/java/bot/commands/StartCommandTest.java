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

public class StartCommandTest {

    private final UserMessageProcessor processor = new DefaultUserMessageProcessor();
    private final Command startCommand = new StartCommand(processor);

    @Test
    @DisplayName("Command name and description Test")
    public void shouldReturnNameAndDescription() {
        assertThat(startCommand.command()).isEqualTo("/start");
        assertThat(startCommand.description()).isEqualTo("Command to start the bot");
    }

    @Test
    @DisplayName("Bot start test")
    public void shouldReturnCorrectResponse() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/start");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(10L);
        SendMessage response = startCommand.handle(update);
        assertThat(response.getParameters()
            .get("text")).isEqualTo("Bot Started! Now you can track the available sites.");
    }
}
