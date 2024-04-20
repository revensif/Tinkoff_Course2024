package edu.java.scrapper.service.notification;

import edu.java.client.bot.HttpBotClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.service.notification.GeneralNotificationService;
import edu.java.service.queue.ScrapperQueueProducer;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeneralNotificationServiceTest {

    private static final LinkUpdateRequest REQUEST = new LinkUpdateRequest(
        1L,
        URI.create("link1.com"),
        "description",
        List.of()
    );

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private ScrapperQueueProducer queueProducer;

    @Mock
    private HttpBotClient client;

    @InjectMocks
    private GeneralNotificationService notificationService;

    @Test
    public void shouldSendUpdateWithQueueProducer() {
        //arrange
        when(applicationConfig.useQueue()).thenReturn(true);
        //act
        notificationService.sendUpdate(REQUEST);
        //assert
        verify(queueProducer, times(1)).sendUpdate(REQUEST);
        verify(client, times(0)).sendUpdate(REQUEST);
    }

    @Test
    public void shouldSendUpdateWithHttpBotClient() {
        //arrange
        when(applicationConfig.useQueue()).thenReturn(false);
        //act
        notificationService.sendUpdate(REQUEST);
        //assert
        verify(client, times(1)).sendUpdate(REQUEST);
        verify(queueProducer, times(0)).sendUpdate(REQUEST);
    }
}
