package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.processor.UserMessageProcessor;
import edu.java.bot.service.LinkParser;
import edu.java.bot.service.LinkValidator;
import edu.java.bot.service.MessageParser;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Component
public class UntrackCommandTest {

    private final ApplicationConfig applicationConfig = new ApplicationConfig(
        System.getenv("TOKEN"),
        List.of("github.com")
    );
    private final LinkParser linkParser = new LinkParser();
    private final LinkValidator linkValidator = new LinkValidator(applicationConfig.resources());
    private final MessageParser messageParser = new MessageParser(linkValidator);
    @Autowired
    private UserMessageProcessor processor;
    private final Command untrackCommand = new UntrackCommand(processor, linkParser, messageParser);
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
    @DisplayName("Command name and description Test")
    public void shouldReturnNameAndDescription() {
        assertThat(untrackCommand.command()).isEqualTo("/untrack");
        assertThat(untrackCommand.description()).isEqualTo("Command to stop tracking the link");
    }

    @Test
    @DisplayName("Link untracking test : Correct link")
    public void shouldReturnCorrectResponse() {
        String link = "https://github.com/revensif/Tinkoff_Course2024";
        when(message.text()).thenReturn("/untrack " + link);
        SendMessage response = untrackCommand.handle(update);
        assertThat(response.getParameters().get("text")).isEqualTo("The link " + link + " is no longer being tracked");
    }

    @Test
    @DisplayName("Link untracking test : Incorrect link")
    public void shouldReturnIncorrectResponse() {
        String link = "https://mail.ru/";
        when(message.text()).thenReturn("/untrack " + link);
        SendMessage response = untrackCommand.handle(update);
        assertThat(response.getParameters().get("text")).isEqualTo(
            "Incorrect input, try /track https://stackoverflow.com");
    }
}
