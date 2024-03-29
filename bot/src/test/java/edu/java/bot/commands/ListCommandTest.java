package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.HttpScrapperClient;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListCommandTest {

    private final HttpScrapperClient client = mock(HttpScrapperClient.class);
    private final UserMessageProcessor processor = new DefaultUserMessageProcessor(client);
    private final Command listCommand = new ListCommand(processor, client);

    @Test
    @DisplayName("Command name and description Test")
    public void shouldReturnNameAndDescription() {
        assertThat(listCommand.command()).isEqualTo("/list");
        assertThat(listCommand.description()).isEqualTo("Command to show all tracked links");
    }

    @Test
    @DisplayName("All tracked links test")
    public void shouldReturnCorrectResponse() {
        //arrange
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/list");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(10L);
        Mono<ListLinksResponse> linksResponse = Mono.just(
            new ListLinksResponse(null, 0)
        );
        when(client.getAllLinks(any(Long.class))).thenReturn(linksResponse);
        //act
        SendMessage response = listCommand.handle(update);
        //assert
        assertThat(response.getParameters().get("text")).isEqualTo("You are not tracking any links");
    }
}
