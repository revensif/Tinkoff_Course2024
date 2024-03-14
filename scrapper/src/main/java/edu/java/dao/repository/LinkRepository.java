package edu.java.dao.repository;

import edu.java.dto.Link;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkRepository {

    Link add(URI url);

    Link remove(URI url);

    List<Link> findAll();

    Link findByUri(URI url);

    void changeUpdatedAt(URI url, OffsetDateTime updatedAt);

    List<Link> findOutdatedLinks(Duration threshold);
}
