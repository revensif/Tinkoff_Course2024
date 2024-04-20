package edu.java.dao.repository.jpa;

import edu.java.dao.repository.QuestionRepository;
import edu.java.dao.repository.jpa.inner_repository.InnerJpaQuestionRepository;
import edu.java.dto.Question;
import edu.java.dto.entity.QuestionEntity;
import edu.java.utils.EntityUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import static edu.java.utils.EntityUtils.createQuestionEntity;
import static edu.java.utils.EntityUtils.questionToQuestionEntity;

@RequiredArgsConstructor
public class JpaQuestionRepository implements QuestionRepository {

    private final InnerJpaQuestionRepository questionRepository;

    @Override
    public Question addQuestion(long linkId, int answerCount, int commentCount) {
        questionRepository.saveAndFlush(createQuestionEntity(linkId, answerCount, commentCount));
        return findByLinkId(linkId);
    }

    @Override
    public Question findByLinkId(long linkId) {
        Optional<QuestionEntity> questionEntity = questionRepository.findById(linkId);
        return questionEntity.map(EntityUtils::questionEntityToQuestion).orElse(null);
    }

    @Override
    public Question removeQuestion(long linkId) {
        Question removedQuestion = findByLinkId(linkId);
        if (removedQuestion == null) {
            return null;
        }
        questionRepository.delete(questionToQuestionEntity(removedQuestion));
        return removedQuestion;
    }

    @Override
    public void changeAnswerCount(long linkId, int answerCount) {
        questionRepository.changeAnswerCount(linkId, answerCount);
    }

    @Override
    public void changeCommentCount(long linkId, int commentCount) {
        questionRepository.changeCommentCount(linkId, commentCount);
    }
}
