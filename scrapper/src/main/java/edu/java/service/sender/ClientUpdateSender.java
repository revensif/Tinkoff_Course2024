package edu.java.service.sender;

import edu.java.client.bot.HttpBotClient;
import edu.java.dto.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(ScrapperUpdateSender.class)
@RequiredArgsConstructor
public class ClientUpdateSender implements UpdateSender {

    private final HttpBotClient botClient;

    @Override
    public void sendUpdate(LinkUpdateRequest request) {
        botClient.sendUpdate(request);
    }
}
