package edu.java.dao.repository.mapper;

import edu.java.dto.ChatLink;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ChatLinkRowMapper implements RowMapper<ChatLink> {

    @Override
    public ChatLink mapRow(ResultSet rs, int rowNum) throws SQLException {
        long tgChatId = rs.getLong("chat_id");
        long linkId = rs.getLong("link_id");
        return new ChatLink(tgChatId, linkId);
    }
}
