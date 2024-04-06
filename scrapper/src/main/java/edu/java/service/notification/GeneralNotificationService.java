package edu.java.service.notification;

import edu.java.client.bot.HttpBotClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.service.queue.ScrapperQueueProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneralNotificationService {

    private final ApplicationConfig applicationConfig;
    private final ScrapperQueueProducer queueProducer;
    private final HttpBotClient botClient;

    public void sendUpdate(LinkUpdateRequest request) {
        if (applicationConfig.useQueue()) {
            queueProducer.sendUpdate(request);
        } else {
            botClient.sendUpdate(request);
        }
    }
}
