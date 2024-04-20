package edu.java.dao.repository.jpa.inner_repository;

import edu.java.dto.entity.ChatLinkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InnerJpaChatLinkRepository extends JpaRepository<ChatLinkEntity, Long> {

    Optional<ChatLinkEntity> findChatLinkEntityByChatIdAndLinkId(long chatId, long linkId);

    List<ChatLinkEntity> findChatLinkEntitiesByLinkId(long linkId);

    List<ChatLinkEntity> findChatLinkEntitiesByChatId(long chatId);
}
