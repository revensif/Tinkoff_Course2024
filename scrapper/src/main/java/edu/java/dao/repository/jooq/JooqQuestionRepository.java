package edu.java.dao.repository.jooq;

import edu.java.dao.repository.QuestionRepository;
import edu.java.dto.Question;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.dao.jooq.Tables.QUESTION;

@Repository
@RequiredArgsConstructor
public class JooqQuestionRepository implements QuestionRepository {

    private final DSLContext dslContext;

    @Override
    public Question addQuestion(long linkId, int answerCount, int commentCount) {
        dslContext.insertInto(QUESTION)
            .set(QUESTION.LINK_ID, linkId)
            .set(QUESTION.ANSWER_COUNT, answerCount)
            .set(QUESTION.COMMENT_COUNT, commentCount)
            .execute();
        return findByLinkId(linkId);
    }

    @Override
    public Question findByLinkId(long linkId) {
        return dslContext.select(QUESTION.fields())
            .from(QUESTION)
            .where(QUESTION.LINK_ID.eq(linkId))
            .fetchOneInto(Question.class);
    }

    @Override
    public Question removeQuestion(long linkId) {
        Question removedQuestion = findByLinkId(linkId);
        dslContext.deleteFrom(QUESTION)
            .where(QUESTION.LINK_ID.eq(linkId))
            .execute();
        return removedQuestion;
    }

    @Override
    public void changeAnswerCount(long linkId, int answerCount) {
        dslContext.update(QUESTION)
            .set(QUESTION.ANSWER_COUNT, answerCount)
            .where(QUESTION.LINK_ID.eq(linkId))
            .execute();
    }

    @Override
    public void changeCommentCount(long linkId, int commentCount) {
        dslContext.update(QUESTION)
            .set(QUESTION.COMMENT_COUNT, commentCount)
            .where(QUESTION.LINK_ID.eq(linkId))
            .execute();
    }
}
