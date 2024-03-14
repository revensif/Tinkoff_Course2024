package edu.java.dao.repository.jdbc;

import edu.java.dao.repository.ChatRepository;
import edu.java.dao.repository.mapper.ChatRowMapper;
import edu.java.dto.Chat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatRepository implements ChatRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Chat add(long tgChatId) {
        jdbcTemplate.update("INSERT INTO chat VALUES (?)", tgChatId);
        return findById(tgChatId);
    }

    @Override
    public Chat remove(long tgChatId) {
        Chat removedChat = findById(tgChatId);
        jdbcTemplate.update("DELETE FROM chat WHERE chat_id = ?", tgChatId);
        return removedChat;
    }

    @Override
    public List<Chat> findAll() {
        return jdbcTemplate.query("SELECT * FROM chat", new ChatRowMapper());
    }

    @Override
    public Chat findById(long tgChatId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM chat WHERE chat_id = ?", new ChatRowMapper(), tgChatId);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }
}
