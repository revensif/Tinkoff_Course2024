package edu.java.service.jdbc;

import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dto.Link;
import edu.java.dto.request.AddLinkRequest;
import edu.java.dto.request.RemoveLinkRequest;
import edu.java.dto.response.LinkResponse;
import edu.java.dto.response.ListLinksResponse;
import edu.java.exception.LinkAlreadyTrackedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.service.LinksService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcLinksService implements LinksService {

    private final JdbcLinkRepository linkRepository;
    private final JdbcChatLinkRepository chatLinkRepository;

    @Override
    @Transactional
    public LinkResponse add(long tgChatId, AddLinkRequest request) {
        Link link = linkRepository.findByUri(request.url());
        if ((link != null) && (chatLinkRepository.findByChatAndLinkIds(tgChatId, link.linkId()) != null)) {
            throw new LinkAlreadyTrackedException();
        }
        if (link == null) {
            link = linkRepository.add(request.url());
        }
        chatLinkRepository.add(tgChatId, link.linkId());
        return new LinkResponse(link.linkId(), link.url());
    }

    @Override
    @Transactional
    public LinkResponse remove(long tgChatId, RemoveLinkRequest request) {
        Link link = linkRepository.findByUri(request.url());
        if ((link == null) || (chatLinkRepository.findByChatAndLinkIds(tgChatId, link.linkId()) == null)) {
            throw new LinkNotFoundException();
        }
        chatLinkRepository.remove(tgChatId, link.linkId());
        if (chatLinkRepository.findAllChatsThatTrackThisLink(link.linkId()).isEmpty()) {
            linkRepository.remove(request.url());
        }
        return new LinkResponse(link.linkId(), link.url());
    }

    @Override
    @Transactional
    public ListLinksResponse listAll(long tgChatId) {
        List<LinkResponse> links = chatLinkRepository.findAllLinksTrackedByThisChat(tgChatId).stream()
            .map((link) -> new LinkResponse(link.linkId(), link.url()))
            .toList();
        return new ListLinksResponse(links, links.size());
    }
}
