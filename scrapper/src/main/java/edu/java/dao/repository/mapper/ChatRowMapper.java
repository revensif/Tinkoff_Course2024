package edu.java.dao.repository.mapper;

import edu.java.dto.Chat;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ChatRowMapper implements RowMapper<Chat> {

    @Override
    public Chat mapRow(ResultSet rs, int rowNum) throws SQLException {
        long tgChatId = rs.getLong("chat_id");
        return new Chat(tgChatId);
    }
}
