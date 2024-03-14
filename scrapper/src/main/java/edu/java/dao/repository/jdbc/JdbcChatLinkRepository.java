package edu.java.dao.repository.jdbc;

import edu.java.dao.repository.ChatLinkRepository;
import edu.java.dao.repository.mapper.ChatLinkRowMapper;
import edu.java.dto.ChatLink;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatLinkRepository implements ChatLinkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public ChatLink add(long tgChatId, long linkId) {
        jdbcTemplate.update("INSERT INTO chat_link VALUES (?, ?)", tgChatId, linkId);
        return new ChatLink(tgChatId, linkId);
    }

    @Override
    public ChatLink remove(long tgChatId, long linkId) {
        ChatLink chatLink = findByChatAndLinkIds(tgChatId, linkId);
        jdbcTemplate.update("DELETE FROM chat_link WHERE chat_id = ? AND link_id = ?", tgChatId, linkId);
        return chatLink;
    }

    @Override
    public List<ChatLink> findAll() {
        return jdbcTemplate.query("SELECT * FROM chat_link", new ChatLinkRowMapper());
    }

    @Override
    public ChatLink findByChatAndLinkIds(long tgChatId, long linkId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM chat_link WHERE chat_id = ? AND link_id = ?",
                new ChatLinkRowMapper(),
                tgChatId, linkId
            );
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Override
    public List<ChatLink> findAllChatsThatTrackThisLink(long linkId) {
        return jdbcTemplate.query("SELECT * FROM chat_link WHERE link_id = ?", new ChatLinkRowMapper(), linkId);
    }

    @Override
    public List<ChatLink> findAllLinksTrackedByThisChat(long tgChatId) {
        return jdbcTemplate.query("SELECT * FROM chat_link WHERE chat_id = ?", new ChatLinkRowMapper(), tgChatId);
    }
}
