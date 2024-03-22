package edu.java.dao.repository.jooq;

import edu.java.dao.repository.LinkRepository;
import edu.java.dto.Link;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.dao.jooq.Tables.LINK;

@Repository
@RequiredArgsConstructor
public class JooqLinkRepository implements LinkRepository {

    private final DSLContext dslContext;

    @Override
    public Link add(URI url) {
        dslContext.insertInto(LINK)
            .set(LINK.URL, url.toString())
            .set(LINK.UPDATED_AT, OffsetDateTime.now())
            .execute();
        return findByUri(url);
    }

    @Override
    public Link remove(URI url) {
        Link removedLink = findByUri(url);
        dslContext.deleteFrom(LINK)
            .where(LINK.URL.eq(url.toString()))
            .execute();
        return removedLink;
    }

    @Override
    public List<Link> findAll() {
        return dslContext.select(LINK.fields())
            .from(LINK)
            .fetchInto(Link.class);
    }

    @Override
    public Link findByUri(URI url) {
        return dslContext.select(LINK.fields())
            .from(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetchOneInto(Link.class);
    }

    @Override
    public void changeUpdatedAt(URI url, OffsetDateTime updatedAt) {
        dslContext.update(LINK)
            .set(LINK.UPDATED_AT, updatedAt)
            .where(LINK.URL.eq(url.toString()))
            .execute();
    }

    @Override
    public List<Link> findOutdatedLinks(Duration threshold) {
        OffsetDateTime thresholdTime = LocalDateTime.now().minus(threshold).atOffset(OffsetDateTime.now().getOffset());
        return dslContext.select(LINK.fields())
            .from(LINK)
            .where(LINK.UPDATED_AT.lessThan(thresholdTime))
            .fetchInto(Link.class);
    }
}
