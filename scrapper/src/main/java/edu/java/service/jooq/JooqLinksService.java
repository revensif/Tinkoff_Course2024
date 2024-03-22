package edu.java.service.jooq;

import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jooq.JooqChatLinkRepository;
import edu.java.dao.repository.jooq.JooqLinkRepository;
import edu.java.dao.repository.jooq.JooqQuestionRepository;
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
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Transactional
public class JooqLinksService implements LinksService {

    private final StackOverflowClient client;
    private final JooqQuestionRepository questionRepository;
    private final JooqLinkRepository linkRepository;
    private final JooqChatLinkRepository chatLinkRepository;
    private final List<String> resources;

    @Override
    public LinkResponse add(long tgChatId, AddLinkRequest request) {
        Link link = linkRepository.findByUri(request.url());
        if ((link != null) && (chatLinkRepository.findByChatAndLinkIds(tgChatId, link.linkId()) != null)) {
            throw new LinkAlreadyTrackedException();
        }
        if (link == null) {
            link = linkRepository.add(request.url());
            if (link.url().getHost().matches(resources.getLast())) {
                long linkId = link.linkId();
                Mono.zip(client.fetchQuestion(linkId), client.fetchComments(linkId))
                    .doOnNext(response -> questionRepository.addQuestion(
                        linkId,
                        response.getT1().items().getFirst().answerCount(),
                        response.getT2().items().size()
                    ))
                    .subscribe();
            }
        }
        chatLinkRepository.add(tgChatId, link.linkId());
        return new LinkResponse(link.linkId(), link.url());
    }

    @Override
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
    public ListLinksResponse listAll(long tgChatId) {
        List<LinkResponse> links = chatLinkRepository.findAllLinksTrackedByThisChat(tgChatId).stream()
            .map((link) -> new LinkResponse(link.linkId(), link.url()))
            .toList();
        return new ListLinksResponse(links, links.size());
    }
}
