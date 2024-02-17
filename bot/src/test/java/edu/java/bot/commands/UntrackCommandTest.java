package commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UntrackCommandTest {

    private final UserMessageProcessor processor = new DefaultUserMessageProcessor();

    @Test
    @DisplayName("Command name and description Test")
    public void shouldReturnNameAndDescription() {
        Command untrackCommand = new UntrackCommand(processor);
        assertThat(untrackCommand.command()).isEqualTo("/untrack <link>");
        assertThat(untrackCommand.description()).isEqualTo("Command to stop tracking the link");
    }

    @Test
    @DisplayName("Link untracking test : Correct link")
    public void shouldReturnCorrectResponse() {
        String link = "https://github.com/revensif/Tinkoff_Course2024";
        Command untrackCommand = new UntrackCommand(processor);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/untrack " + link);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(10L);
        SendMessage response = untrackCommand.handle(update);
        assertThat(response.getParameters().get("text")).isEqualTo("The link " + link + " is no longer being tracked");
    }

    @Test
    @DisplayName("Link untracking test : Incorrect link")
    public void shouldReturnIncorrectResponse() {
        String link = "https://mail.ru/";
        Command untrackCommand = new UntrackCommand(processor);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/untrack " + link);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(10L);
        SendMessage response = untrackCommand.handle(update);
        assertThat(response.getParameters().get("text")).isEqualTo(
            "Incorrect input, try /track https://stackoverflow.com");
    }
}
