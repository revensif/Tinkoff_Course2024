package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import java.net.URI;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrackCommandTest {

    private final MeterRegistry registry = new SimpleMeterRegistry();
    private final HttpScrapperClient client = mock(HttpScrapperClient.class);
    private final UserMessageProcessor processor = new DefaultUserMessageProcessor(client, registry);
    private final Command trackCommand = new TrackCommand(processor, client);
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
        assertThat(trackCommand.command()).isEqualTo("/track");
        assertThat(trackCommand.description()).isEqualTo("Command to track the link");
    }

    @Test
    @DisplayName("Link tracking test : Correct link")
    public void shouldReturnCorrectResponse() {
        //arrange
        URI link = URI.create("https://github.com/revensif/Tinkoff_Course2024");
        Mono<LinkResponse> responseMono = Mono.just(new LinkResponse(10L, link));
        when(message.text()).thenReturn("/track " + link);
        when(client.addLink(any(Long.class), any(AddLinkRequest.class))).thenReturn(responseMono);
        //act
        SendMessage response = trackCommand.handle(update);
        //assert
        assertThat(response.getParameters().get("text")).isEqualTo("Now you are tracking this link");
    }

    @Test
    @DisplayName("Link tracking test : Incorrect link")
    public void shouldReturnIncorrectResponse() {
        //arrange
        String link = "https://mail.ru/";
        when(message.text()).thenReturn("/track " + link);
        //act
        SendMessage response = trackCommand.handle(update);
        //assert
        assertThat(response.getParameters().get("text")).isEqualTo(
            "Incorrect input, try /track https://stackoverflow.com");
    }
}
