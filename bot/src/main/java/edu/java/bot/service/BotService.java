package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dto.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class BotService {

    private final Bot bot;

    public void sendUpdate(LinkUpdateRequest request) {
        StringBuilder stringBuilder = new StringBuilder("An update has occurred!\n");
        stringBuilder.append(request.id())
            .append(": ").append(request.url())
            .append("\nDescription: \n").append(request.description());
        request.tgChatIds().forEach((chatId) -> bot.execute(new SendMessage(chatId, String.valueOf(stringBuilder))));
    }
}
