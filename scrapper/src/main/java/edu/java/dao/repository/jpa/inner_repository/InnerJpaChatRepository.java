package edu.java.dao.repository.jpa.inner_repository;

import edu.java.dto.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InnerJpaChatRepository extends JpaRepository<ChatEntity, Long> {
}
