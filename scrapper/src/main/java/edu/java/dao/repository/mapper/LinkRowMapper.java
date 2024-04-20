package edu.java.dao.repository.mapper;

import edu.java.dto.Link;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.jdbc.core.RowMapper;

public class LinkRowMapper implements RowMapper<Link> {

    private static final ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();

    @Override
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
        long linkId = rs.getLong("link_id");
        URI url = URI.create(rs.getString("url"));
        OffsetDateTime dateTime = rs.getTimestamp("updated_at")
            .toLocalDateTime()
            .atOffset(ZONE_OFFSET);
        return new Link(linkId, url, dateTime);
    }
}
