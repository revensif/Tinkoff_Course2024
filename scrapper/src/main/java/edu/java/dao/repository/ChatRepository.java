package edu.java.dao.repository;

import edu.java.dto.Chat;
import java.util.List;

public interface ChatRepository {

    Chat add(long tgChatId);

    Chat remove(long tgChatId);

    List<Chat> findAll();

    Chat findById(long tgChatId);
}
