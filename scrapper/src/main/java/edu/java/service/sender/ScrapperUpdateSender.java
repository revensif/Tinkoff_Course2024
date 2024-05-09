package edu.java.service.sender;

import edu.java.dto.request.LinkUpdateRequest;
import edu.java.service.queue.ScrapperQueueProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "app.use-queue", havingValue = "true")
@RequiredArgsConstructor
public class ScrapperUpdateSender implements UpdateSender {

    private final ScrapperQueueProducer producer;

    @Override
    public void sendUpdate(LinkUpdateRequest request) {
        producer.sendUpdate(request);
    }
}
