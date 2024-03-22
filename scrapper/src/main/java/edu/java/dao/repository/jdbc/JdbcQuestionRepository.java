package edu.java.dao.repository.jdbc;

import edu.java.dao.repository.QuestionRepository;
import edu.java.dao.repository.mapper.QuestionRowMapper;
import edu.java.dto.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcQuestionRepository implements QuestionRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Question addQuestion(long linkId, int answerCount, int commentCount) {
        jdbcTemplate.update("INSERT INTO question VALUES (?, ?, ?)", linkId, answerCount, commentCount);
        return findByLinkId(linkId);
    }

    @Override
    public Question findByLinkId(long linkId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM question WHERE link_id = ?",
                new QuestionRowMapper(),
                linkId
            );
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Override
    public Question removeQuestion(long linkId) {
        Question removedQuestion = findByLinkId(linkId);
        jdbcTemplate.update("DELETE FROM question WHERE link_id = ?", linkId);
        return removedQuestion;
    }

    @Override
    public void changeAnswerCount(long linkId, int answerCount) {
        Question question = findByLinkId(linkId);
        if (question == null) {
            return;
        }
        jdbcTemplate.update("UPDATE question SET answer_count = ? WHERE link_id = ?", answerCount, linkId);
    }

    @Override
    public void changeCommentCount(long linkId, int commentCount) {
        Question question = findByLinkId(linkId);
        if (question == null) {
            return;
        }
        jdbcTemplate.update("UPDATE question SET comment_count = ? WHERE link_id = ?", commentCount, linkId);
    }
}
