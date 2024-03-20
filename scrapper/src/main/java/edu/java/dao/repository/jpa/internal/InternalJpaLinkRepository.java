package edu.java.dao.repository.jpa.internal;

import edu.java.dao.repository.jpa.entity.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalJpaLinkRepository extends JpaRepository<LinkEntity, Long> {
}
