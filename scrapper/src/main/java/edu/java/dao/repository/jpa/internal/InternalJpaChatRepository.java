package edu.java.dao.repository.jpa.internal;

import edu.java.dao.repository.jpa.JpaChatRepository;
import edu.java.dao.repository.jpa.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalJpaChatRepository extends JpaRepository<ChatEntity, Long> {
}
