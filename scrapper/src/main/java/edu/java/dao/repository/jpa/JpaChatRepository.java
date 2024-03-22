package edu.java.dao.repository.jpa;

import edu.java.dto.entity.ChatEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatRepository extends JpaRepository<ChatEntity, Long> {

    List<ChatEntity> findAllByLinksLinkId(long linkId);
}
