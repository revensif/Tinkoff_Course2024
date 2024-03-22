package edu.java.service.jdbc;

import edu.java.dao.repository.jdbc.JdbcChatRepository;
import edu.java.dto.Chat;
import edu.java.exception.ChatAlreadyRegisteredException;
import edu.java.exception.ChatNotFoundException;
import edu.java.service.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
public class JdbcTgChatService implements TgChatService {

    private final JdbcChatRepository chatRepository;

    @Override
    public Chat register(long tgChatId) {
        if (chatRepository.findById(tgChatId) != null) {
            throw new ChatAlreadyRegisteredException();
        }
        return chatRepository.add(tgChatId);
    }

    @Override
    public Chat unregister(long tgChatId) {
        if (chatRepository.findById(tgChatId) == null) {
            throw new ChatNotFoundException();
        }
        return chatRepository.remove(tgChatId);
    }
}
