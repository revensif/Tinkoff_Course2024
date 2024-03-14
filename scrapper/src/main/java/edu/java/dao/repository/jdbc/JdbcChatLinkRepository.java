package edu.java.dao.repository.jdbc;

import edu.java.dao.repository.ChatLinkRepository;
import edu.java.dao.repository.mapper.ChatLinkRowMapper;
import edu.java.dao.repository.mapper.LinkRowMapper;
import edu.java.dto.ChatLink;
import edu.java.dto.Link;
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
    public List<Long> findAllChatsThatTrackThisLink(long linkId) {
        return jdbcTemplate.queryForList("SELECT chat_id FROM chat_link WHERE link_id = ?", Long.class, linkId);
    }

    @Override
    public List<Link> findAllLinksTrackedByThisChat(long tgChatId) {
        return jdbcTemplate.query(
            "SELECT * FROM link l JOIN chat_link cl on l.link_id = cl.link_id WHERE cl.chat_id = ?",
            new LinkRowMapper(),
            tgChatId
        );
    }
}
