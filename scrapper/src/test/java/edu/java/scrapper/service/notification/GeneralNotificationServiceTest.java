package edu.java.scrapper.service.notification;

import edu.java.dto.request.LinkUpdateRequest;
import edu.java.service.notification.GeneralNotificationService;
import edu.java.service.sender.ClientUpdateSender;
import edu.java.service.sender.ScrapperUpdateSender;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GeneralNotificationServiceTest {

    private static final LinkUpdateRequest REQUEST = new LinkUpdateRequest(
        1L,
        URI.create("link1.com"),
        "description",
        List.of()
    );

    @Mock
    private ScrapperUpdateSender scrapperUpdateSender;

    @Mock
    private ClientUpdateSender clientUpdateSender;

    @Test
    public void shouldSendUpdateWithQueueProducer() {
        //arrange
        GeneralNotificationService notificationService = new GeneralNotificationService(scrapperUpdateSender);
        //act
        notificationService.sendUpdate(REQUEST);
        //assert
        verify(scrapperUpdateSender, times(1)).sendUpdate(REQUEST);
        verify(clientUpdateSender, times(0)).sendUpdate(REQUEST);
    }

    @Test
    public void shouldSendUpdateWithHttpBotClient() {
        //arrange
        GeneralNotificationService notificationService = new GeneralNotificationService(clientUpdateSender);
        //act
        notificationService.sendUpdate(REQUEST);
        //assert
        verify(clientUpdateSender, times(1)).sendUpdate(REQUEST);
        verify(scrapperUpdateSender, times(0)).sendUpdate(REQUEST);
    }
}
