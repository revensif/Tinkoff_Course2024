package edu.java.dao.repository.jpa;

import edu.java.dto.entity.LinkEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface JpaLinkRepository extends JpaRepository<LinkEntity, Long> {

    Optional<LinkEntity> findByUrl(String url);

    List<LinkEntity> findAllByChatsChatId(Long chatId);

    @Query("SELECT l FROM LinkEntity l WHERE l.updatedAt < :threshold")
    List<LinkEntity> findOutdatedLinks(OffsetDateTime threshold);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE LinkEntity SET updatedAt = :updatedAt WHERE url = :url")
    void changeUpdatedAt(String url, OffsetDateTime updatedAt);

    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO chat_link (chat_id, link_id) VALUES (:chatId, :linkId)", nativeQuery = true)
    void addChatLink(long chatId, long linkId);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM chat_link WHERE chat_id = :chatId AND link_id = :linkId", nativeQuery = true)
    void removeChatLink(long chatId, long linkId);
}
