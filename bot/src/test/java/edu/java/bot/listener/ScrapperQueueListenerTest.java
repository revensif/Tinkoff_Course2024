package edu.java.bot.listener;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.BotService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScrapperQueueListenerTest {

    @Mock
    private BotService botService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private ScrapperQueueListener queueListener;

    @Test
    public void shouldListenAndSendOneUpdate() {
        //arrange
        LinkUpdateRequest request = new LinkUpdateRequest(
            1L,
            URI.create("link1.com"),
            "description",
            List.of(1L)
        );
        //act
        queueListener.listen(request, acknowledgment);
        //assert
        verify(botService, times(1)).sendUpdate(request);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void shouldListenAndThrowErrorAfterIncorrectLink() {
        //arrange
        LinkUpdateRequest request = new LinkUpdateRequest(0L, null, null, null);
        //act
        assertThrows(RuntimeException.class, () -> queueListener.listen(request, acknowledgment));
        //assert
        verify(botService, times(0)).sendUpdate(request);
        verify(acknowledgment, times(1)).acknowledge();
    }
}
