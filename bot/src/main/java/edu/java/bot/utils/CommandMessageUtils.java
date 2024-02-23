package edu.java.bot.utils;

import java.net.URI;
import lombok.SneakyThrows;
import static edu.java.bot.utils.LinkUtils.isCorrectLink;

public final class CommandMessageUtils {

    private CommandMessageUtils() {
    }

    @SneakyThrows
    public static URI getURIFromMessage(String[] messageParts) {
        if (messageParts.length != 2) {
            return null;
        }
        URI url = new URI(messageParts[1]);
        if (!isCorrectLink(url)) {
            return null;
        }
        return url;
    }
}
