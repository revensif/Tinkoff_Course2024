package edu.java.dao.repository.jpa;

import edu.java.dao.repository.ChatRepository;
import edu.java.dao.repository.jpa.entity.ChatEntity;
import edu.java.dao.repository.jpa.internal.InternalJpaChatRepository;
import edu.java.dto.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaChatRepository implements ChatRepository {

    private final InternalJpaChatRepository chatRepository;

    @Override
    public Chat add(long tgChatId) {
        ChatEntity chat = chatRepository.save(new ChatEntity(tgChatId));
        return new Chat(chat.getChatId());
    }

    @Override
    public Chat remove(long tgChatId) {
        return null;
    }

    @Override
    public List<Chat> findAll() {
        return null;
    }

    @Override
    public Chat findById(long tgChatId) {
        return null;
    }
}
