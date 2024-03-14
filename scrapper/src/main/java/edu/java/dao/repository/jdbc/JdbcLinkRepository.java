package edu.java.dao.repository.jdbc;

import edu.java.dao.repository.LinkRepository;
import edu.java.dao.repository.mapper.LinkRowMapper;
import edu.java.dto.Link;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
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
    public Link add(URI url) {
        jdbcTemplate.update(
            "INSERT INTO link (url, updated_at) VALUES (?, ?)",
            url.toString(),
            OffsetDateTime.MIN.truncatedTo(ChronoUnit.MILLIS)
        );
        return findByUri(url);
    }

    @Override
    public Link remove(URI url) {
        Link link = findByUri(url);
        jdbcTemplate.update("DELETE FROM link WHERE url = ?", url.toString());
        return link;
    }

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.query("SELECT * FROM link", new LinkRowMapper());
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

    @Override
    public List<Link> findOutdatedLinks(Duration threshold) {
        OffsetDateTime thresholdTime = LocalDateTime.now().minus(threshold).atOffset(OffsetDateTime.now().getOffset());
        return jdbcTemplate.query("SELECT * FROM link WHERE updated_at < ?", new LinkRowMapper(), thresholdTime);
    }
}
