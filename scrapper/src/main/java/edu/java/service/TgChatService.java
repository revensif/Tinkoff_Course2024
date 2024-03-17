package edu.java.service;

import edu.java.dto.Chat;

public interface TgChatService {

    Chat register(long tgChatId);

    Chat unregister(long tgChatId);
}
