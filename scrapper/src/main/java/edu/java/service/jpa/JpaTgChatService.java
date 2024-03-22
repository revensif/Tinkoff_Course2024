package edu.java.service.jpa;

import edu.java.dao.repository.jpa.JpaChatRepository;
import edu.java.dto.Chat;
import edu.java.dto.entity.ChatEntity;
import edu.java.exception.ChatAlreadyRegisteredException;
import edu.java.exception.ChatNotFoundException;
import edu.java.service.TgChatService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
public class JpaTgChatService implements TgChatService {

    private final JpaChatRepository chatRepository;

    @Override
    public Chat register(long tgChatId) {
        if (chatRepository.findById(tgChatId).isPresent()) {
            throw new ChatAlreadyRegisteredException();
        }
        ChatEntity chat = chatRepository.saveAndFlush(
            ChatEntity.builder()
                .chatId(tgChatId)
                .build()
        );
        return new Chat(chat.getChatId());
    }

    @Override
    public Chat unregister(long tgChatId) {
        Optional<ChatEntity> chatOptional = chatRepository.findById(tgChatId);
        if (chatOptional.isEmpty()) {
            throw new ChatNotFoundException();
        }
        ChatEntity removedChat = chatOptional.get();
        chatRepository.delete(removedChat);
        return new Chat(removedChat.getChatId());
    }
}
