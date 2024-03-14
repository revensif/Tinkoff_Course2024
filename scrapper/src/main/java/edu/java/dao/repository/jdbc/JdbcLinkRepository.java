package edu.java.dao.repository.jdbc;

import edu.java.dao.repository.LinkRepository;
import edu.java.dao.repository.mapper.LinkRowMapper;
import edu.java.dto.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcLinkRepository implements LinkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Link add(long tgChatId, URI url) {
        jdbcTemplate.update(
            "INSERT INTO link VALUES (?, ?, ?)",
            tgChatId,
            url.toString(),
            OffsetDateTime.MIN.truncatedTo(ChronoUnit.MILLIS)
        );
        return new Link(tgChatId, url, OffsetDateTime.MIN);
    }

    @Override
    public Link remove(long tgChatId, URI url) {
        Link link = findByUri(url);
        jdbcTemplate.update("DELETE FROM link WHERE link_id = ? AND url = ?", tgChatId, url.toString());
        return link;
    }

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.query("SELECT * FROM link", new LinkRowMapper());
    }

    @Override
    public Link findById(long tgChatId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM link WHERE link_id = ?", new LinkRowMapper(), tgChatId);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Override
    public Link findByUri(URI url) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM link WHERE url = ?", new LinkRowMapper(), url.toString());
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Override
    public void changeUpdatedAt(URI url, OffsetDateTime updatedAt) {
        Link link = findByUri(url);
        if (link == null) {
            return;
        }
        jdbcTemplate.update("UPDATE link SET updated_at = ? WHERE url = ?", updatedAt, url.toString());
    }
}
