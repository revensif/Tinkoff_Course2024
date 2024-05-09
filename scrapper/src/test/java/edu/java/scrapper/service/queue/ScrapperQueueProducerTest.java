package edu.java.scrapper.service.queue;

import edu.java.configuration.kafka.KafkaConfigurationProperties;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.service.queue.ScrapperQueueProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.net.URI;
import java.util.List;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScrapperQueueProducerTest {

    private static final String TOPIC = "topic";
    @Mock
    private KafkaTemplate<Integer, LinkUpdateRequest> kafkaTemplate;

    @Mock
    private KafkaConfigurationProperties kafkaProperties;

    @InjectMocks
    private ScrapperQueueProducer scrapperQueueProducer;

    @Test
    public void shouldSendUpdateOneTime() {
        //arrange
        LinkUpdateRequest request = new LinkUpdateRequest(
            1L,
            URI.create("link1.com"),
            "description",
            List.of()
        );
        when(kafkaProperties.topic()).thenReturn(TOPIC);
        //act
        scrapperQueueProducer.sendUpdate(request);
        //assert
        verify(kafkaTemplate, times(1)).send(TOPIC, request);
    }
}
