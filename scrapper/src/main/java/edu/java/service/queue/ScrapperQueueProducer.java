package edu.java.service.queue;

import edu.java.configuration.kafka.KafkaConfigurationProperties;
import edu.java.dto.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class ScrapperQueueProducer {

    private final KafkaTemplate<Integer, LinkUpdateRequest> kafkaTemplate;
    private final KafkaConfigurationProperties kafkaProperties;

    public void sendUpdate(LinkUpdateRequest request) {
        try {
            kafkaTemplate.send(kafkaProperties.topic(), request);
        } catch (Exception e) {
            log.error("An error occurred while trying to send a message to kafka: ", e);
        }
    }
}
