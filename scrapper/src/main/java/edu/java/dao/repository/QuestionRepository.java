package edu.java.dao.repository;

import edu.java.dto.Question;

public interface QuestionRepository {

    Question addQuestion(long linkId, int answerCount, int commentCount);

    Question findByLinkId(long linkId);
}
