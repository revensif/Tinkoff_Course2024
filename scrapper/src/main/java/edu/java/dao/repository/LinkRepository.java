package edu.java.dao.repository;

import edu.java.dto.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkRepository {

    Link add(long tgChatId, URI url);

    Link remove(long tgChatId, URI url);

    List<Link> findAll();

    Link findById(long tgChatId);

    Link findByUri(URI url);

    void changeUpdatedAt(URI url, OffsetDateTime updatedAt);
}
