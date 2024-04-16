package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import edu.java.bot.service.LinkParser;
import edu.java.bot.service.LinkValidator;
import edu.java.bot.service.MessageParser;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListCommandTest {

    private final ApplicationConfig applicationConfig = new ApplicationConfig(System.getenv("TOKEN"), List.of());
    private final LinkParser linkParser = new LinkParser();
    private final LinkValidator linkValidator = new LinkValidator(applicationConfig.resources());
    private final MessageParser messageParser = new MessageParser(linkValidator);
    private final UserMessageProcessor processor = new DefaultUserMessageProcessor(linkParser, messageParser);
    private final Command listCommand = new ListCommand(processor, linkParser, messageParser);

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
        assertThat(response.getParameters().get("text")).isEqualTo("It is not tracked because there is no database");
    }
}
