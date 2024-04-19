package edu.java.dao.repository.jpa;

import edu.java.dao.repository.LinkRepository;
import edu.java.dao.repository.jpa.inner_repository.InnerJpaLinkRepository;
import edu.java.dto.Link;
import edu.java.dto.entity.LinkEntity;
import edu.java.utils.EntityUtils;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import static edu.java.utils.EntityUtils.createLinkEntity;
import static edu.java.utils.EntityUtils.linkToLinkEntity;

@RequiredArgsConstructor
public class JpaLinkRepository implements LinkRepository {

    private final InnerJpaLinkRepository linkRepository;

    @Override
    public Link add(URI url) {
        linkRepository.saveAndFlush(createLinkEntity(url.toString(), OffsetDateTime.now()));
        return findByUri(url);
    }

    @Override
    public Link remove(URI url) {
        Link removedLink = findByUri(url);
        linkRepository.delete(linkToLinkEntity(removedLink));
        return removedLink;
    }

    @Override
    public List<Link> findAll() {
        return linkRepository.findAll()
            .stream()
            .map(EntityUtils::linkEntityToLink)
            .toList();
    }

    @Override
    public Link findByUri(URI url) {
        Optional<LinkEntity> linkEntity = linkRepository.findByUrl(url.toString());
        return linkEntity.map(EntityUtils::linkEntityToLink).orElse(null);
    }

    @Override
    public void changeUpdatedAt(URI url, OffsetDateTime updatedAt) {
        Optional<LinkEntity> linkEntity = linkRepository.findByUrl(url.toString());
        if (linkEntity.isPresent()) {
            linkEntity.get().setUpdatedAt(updatedAt);
            linkRepository.saveAndFlush(linkEntity.get());
        }
    }

    @Override
    public List<Link> findOutdatedLinks(Duration threshold) {
        OffsetDateTime thresholdTime = LocalDateTime.now().minus(threshold).atOffset(OffsetDateTime.now().getOffset());
        return linkRepository.findLinkEntitiesByUpdatedAtIsLessThan(thresholdTime)
            .stream()
            .map(EntityUtils::linkEntityToLink)
            .toList();
    }
}
