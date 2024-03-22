package edu.java.dao.repository.jpa;

import edu.java.dto.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface JpaQuestionRepository extends JpaRepository<QuestionEntity, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE QuestionEntity SET answerCount = :answerCount WHERE linkId = :linkId")
    void changeAnswerCount(long linkId, int answerCount);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE QuestionEntity SET commentCount = :commentCount WHERE linkId = :linkId")
    void changeCommentCount(long linkId, int commentCount);
}
