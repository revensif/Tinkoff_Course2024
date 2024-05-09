package edu.java.bot.service;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageParser {

    private final LinkValidator validator;

    @SneakyThrows
    public URI parseMessage(String[] messageParts) {
        if (messageParts.length != 2) {
            return null;
        }
        URI url = new URI(messageParts[1]);
        if (!validator.validateLink(url)) {
            return null;
        }
        return url;
    }
}
