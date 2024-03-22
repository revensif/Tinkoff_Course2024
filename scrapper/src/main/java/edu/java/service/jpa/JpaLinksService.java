package edu.java.service.jpa;

import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jpa.JpaChatRepository;
import edu.java.dao.repository.jpa.JpaLinkRepository;
import edu.java.dao.repository.jpa.JpaQuestionRepository;
import edu.java.dto.entity.ChatEntity;
import edu.java.dto.entity.LinkEntity;
import edu.java.dto.entity.QuestionEntity;
import edu.java.dto.request.AddLinkRequest;
import edu.java.dto.request.RemoveLinkRequest;
import edu.java.dto.response.LinkResponse;
import edu.java.dto.response.ListLinksResponse;
import edu.java.exception.LinkAlreadyTrackedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.service.LinksService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Transactional
public class JpaLinksService implements LinksService {

    private final StackOverflowClient client;
    private final JpaQuestionRepository questionRepository;
    private final JpaLinkRepository linkRepository;
    private final JpaChatRepository chatRepository;
    private final List<String> resources;

    @Override
    public LinkResponse add(long tgChatId, AddLinkRequest request) {
        Optional<LinkEntity> linkOptional = linkRepository.findByUrl(request.url().toString());
        linkOptional.ifPresent(linkEntity -> {
            if (isChatLinkExist(tgChatId, linkEntity)) {
                throw new LinkAlreadyTrackedException();
            }
        });
        LinkEntity link = linkOptional.orElseGet(() -> {
                LinkEntity linkEntity = linkRepository.saveAndFlush(
                    LinkEntity.builder()
                        .url(request.url().toString())
                        .updatedAt(OffsetDateTime.now())
                        .build());
                if (request.url().getHost().matches(resources.getLast())) {
                    long linkId = linkEntity.getLinkId();
                    Mono.zip(client.fetchQuestion(linkId), client.fetchComments(linkId))
                        .publishOn(Schedulers.boundedElastic())
                        .doOnNext(response -> questionRepository.saveAndFlush(
                            QuestionEntity.builder()
                                .linkId(linkId)
                                .answerCount(response.getT1().items().getFirst().answerCount())
                                .commentCount(response.getT2().items().size())
                                .build()
                        ))
                        .subscribe();
                }
                return linkEntity;
            }
        );
        linkRepository.addChatLink(tgChatId, link.getLinkId());
        return new LinkResponse(link.getLinkId(), URI.create(link.getUrl()));
    }

    @Override
    public LinkResponse remove(long tgChatId, RemoveLinkRequest request) {
        Optional<LinkEntity> linkOptional = linkRepository.findByUrl(request.url().toString());
        if (linkOptional.isEmpty()) {
            throw new LinkNotFoundException();
        }
        LinkEntity link = linkOptional.get();
        if (!isChatLinkExist(tgChatId, link)) {
            throw new LinkNotFoundException();
        }
        linkRepository.removeChatLink(tgChatId, link.getLinkId());
        if (chatRepository.findAllByLinksLinkId(link.getLinkId()).isEmpty()) {
            questionRepository.deleteById(link.getLinkId());
            linkRepository.delete(link);
        }
        return new LinkResponse(link.getLinkId(), URI.create(link.getUrl()));
    }

    @Override
    public ListLinksResponse listAll(long tgChatId) {
        List<LinkResponse> linkResponses = linkRepository.findAllByChatsChatId(tgChatId)
            .stream()
            .map(linkEntity -> new LinkResponse(linkEntity.getLinkId(), URI.create(linkEntity.getUrl())))
            .toList();
        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    private boolean isChatLinkExist(long tgChatId, LinkEntity linkEntity) {
        List<ChatEntity> chats = chatRepository.findAllByLinksLinkId(linkEntity.getLinkId());
        return chats.stream()
            .anyMatch(chatEntity -> chatEntity.getChatId() == tgChatId);
    }
}
