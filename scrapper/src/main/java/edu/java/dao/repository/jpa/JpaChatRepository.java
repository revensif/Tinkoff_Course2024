package edu.java.dao.repository.jpa;

import edu.java.dao.repository.ChatRepository;
import edu.java.dao.repository.jpa.inner_repository.InnerJpaChatRepository;
import edu.java.dto.Chat;
import edu.java.dto.entity.ChatEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import static edu.java.utils.EntityUtils.chatToChatEntity;

@RequiredArgsConstructor
public class JpaChatRepository implements ChatRepository {

    private final InnerJpaChatRepository chatRepository;

    @Override
    public Chat add(long tgChatId) {
        chatRepository.saveAndFlush(ChatEntity.builder()
            .chatId(tgChatId)
            .build());
        return findById(tgChatId);
    }

    @Override
    public Chat remove(long tgChatId) {
        Chat removedChat = findById(tgChatId);
        chatRepository.delete(chatToChatEntity(removedChat));
        return removedChat;
    }

    @Override
    public List<Chat> findAll() {
        return chatRepository.findAll()
            .stream()
            .map((chatEntity) -> new Chat(chatEntity.getChatId()))
            .toList();
    }

    @Override
    public Chat findById(long tgChatId) {
        Optional<ChatEntity> chatEntity = chatRepository.findById(tgChatId);
        return chatEntity.map((entity) -> new Chat(entity.getChatId())).orElse(null);
    }
}
