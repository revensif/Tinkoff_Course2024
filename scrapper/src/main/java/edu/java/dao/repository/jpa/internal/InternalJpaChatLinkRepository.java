package edu.java.dao.repository.jpa.internal;

import edu.java.dao.repository.jpa.entity.ChatLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalJpaChatLinkRepository extends JpaRepository<ChatLinkEntity, ChatLinkEntity.ChatLinkEntityId> {
}
