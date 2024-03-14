package edu.java.service.jdbc;

import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcChatRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dto.ChatLink;
import edu.java.dto.Link;
import edu.java.exception.LinkAlreadyTrackedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.service.LinksService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinksService {

    private final JdbcChatRepository chatRepository;
    private final JdbcLinkRepository linkRepository;
    private final JdbcChatLinkRepository chatLinkRepository;

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) {
        Link link = linkRepository.findByUri(url);
        if ((link != null) && (chatLinkRepository.findByChatAndLinkIds(tgChatId, link.getLinkId()) != null)) {
            throw new LinkAlreadyTrackedException();
        } else if (link == null) {
            link = linkRepository.findByUri(url);
            linkRepository.add(tgChatId, url);
        }
        chatLinkRepository.add(tgChatId, link.getLinkId());
        return link;
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, URI url) {
        Link link = linkRepository.findByUri(url);
        if ((link == null) || (chatLinkRepository.findByChatAndLinkIds(tgChatId, link.getLinkId()) == null)) {
            throw new LinkNotFoundException();
        }
        chatLinkRepository.remove(tgChatId, link.getLinkId());
        for (ChatLink chatLink : chatLinkRepository.findAll()) {
            if (chatLink.getLinkId() == link.getLinkId()) {
                return link;
            }
        }
        return linkRepository.remove(tgChatId, url);
    }

    @Override
    @Transactional
    public List<Link> listAll(long tgChatId) {
        return null;
    }
}
