package edu.java.dao.repository.mapper;

import edu.java.dto.Question;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class QuestionRowMapper implements RowMapper<Question> {

    @Override
    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
        long linkId = rs.getLong("link_id");
        int answerCount = rs.getInt("answer_count");
        int commentCount = rs.getInt("comment_count");
        return new Question(linkId, answerCount, commentCount);
    }
}
