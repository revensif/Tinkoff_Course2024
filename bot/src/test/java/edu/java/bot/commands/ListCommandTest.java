package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
@RunWith(SpringRunner.class)
public class ListCommandTest {

    @Autowired
    private Command listCommand;

    @Test
    @DisplayName("Command name and description Test")
    public void shouldReturnNameAndDescription() {
        assertThat(listCommand.command()).isEqualTo("/list");
        assertThat(listCommand.description()).isEqualTo("Command to show all tracked links");
    }

    @Test
    @DisplayName("All tracked links test")
    public void shouldReturnCorrectResponse() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/list");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(10L);
        SendMessage response = listCommand.handle(update);
        assertThat(response.getParameters().get("text")).isEqualTo("You are not tracking any links");
    }
}
