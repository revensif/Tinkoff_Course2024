package edu.java.bot.listener;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class ScrapperQueueListener {

    private final BotService botService;

    @KafkaListener(topics = "${app.scrapper-topic.name}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(LinkUpdateRequest request, Acknowledgment acknowledgment) {
        log.info("A new update has arrived: {}", request);
        try {
            botService.sendUpdate(request);
        } catch (Exception e) {
            log.error("An error occurred while processing the update: ", e);
            throw new RuntimeException(e);
        } finally {
            acknowledgment.acknowledge();
        }
    }
}
