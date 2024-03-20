package edu.java.dao.repository.jpa.internal;

import edu.java.dao.repository.jpa.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalJpaQuestionRepository extends JpaRepository<QuestionEntity, Long> {
}
