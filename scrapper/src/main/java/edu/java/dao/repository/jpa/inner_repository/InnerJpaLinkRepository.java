package edu.java.dao.repository.jpa.inner_repository;

import edu.java.dto.entity.LinkEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InnerJpaLinkRepository extends JpaRepository<LinkEntity, Long> {

    Optional<LinkEntity> findByUrl(String url);

    List<LinkEntity> findLinkEntitiesByUpdatedAtIsLessThan(OffsetDateTime threshold);
}
