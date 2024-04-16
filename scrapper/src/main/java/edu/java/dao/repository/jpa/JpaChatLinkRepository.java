package edu.java.dao.repository.jpa;

import edu.java.dao.repository.ChatLinkRepository;
import edu.java.dao.repository.jpa.inner_repository.InnerJpaChatLinkRepository;
import edu.java.dao.repository.jpa.inner_repository.InnerJpaLinkRepository;
import edu.java.dto.ChatLink;
import edu.java.dto.Link;
import edu.java.dto.entity.ChatLinkEntity;
import edu.java.dto.entity.LinkEntity;
import edu.java.utils.EntityUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import static edu.java.utils.EntityUtils.chatLinkToChatLinkEntity;
import static edu.java.utils.EntityUtils.createChatLinkEntity;

@RequiredArgsConstructor
public class JpaChatLinkRepository implements ChatLinkRepository {

    private final InnerJpaChatLinkRepository chatLinkRepository;
    private final InnerJpaLinkRepository linkRepository;

    @Override
    public ChatLink add(long tgChatId, long linkId) {
        chatLinkRepository.saveAndFlush(createChatLinkEntity(tgChatId, linkId));
        return findByChatAndLinkIds(tgChatId, linkId);
    }

    @Override
    public ChatLink remove(long tgChatId, long linkId) {
        ChatLink removedChatLink = findByChatAndLinkIds(tgChatId, linkId);
        chatLinkRepository.delete(chatLinkToChatLinkEntity(removedChatLink));
        return removedChatLink;
    }

    @Override
    public List<ChatLink> findAll() {
        return chatLinkRepository.findAll()
            .stream()
            .map(EntityUtils::chatLinkEntityToChatLink)
            .toList();
    }

    @Override
    public ChatLink findByChatAndLinkIds(long tgChatId, long linkId) {
        Optional<ChatLinkEntity> chatLinkEntity =
            chatLinkRepository.findChatLinkEntityByChatIdAndLinkId(tgChatId, linkId);
        return chatLinkEntity.map(EntityUtils::chatLinkEntityToChatLink).orElse(null);
    }

    @Override
    public List<Long> findAllChatsThatTrackThisLink(long linkId) {
        return chatLinkRepository.findChatLinkEntitiesByLinkId(linkId)
            .stream()
            .map(ChatLinkEntity::getChatId)
            .toList();
    }

    @Override
    public List<Link> findAllLinksTrackedByThisChat(long tgChatId) {
        List<LinkEntity> linkEntities = linkRepository.findAll();
        List<ChatLinkEntity> chatLinkEntities = chatLinkRepository.findChatLinkEntitiesByChatId(tgChatId);
        return linkEntities.stream()
            .filter(
                linkEntity -> chatLinkEntities
                    .stream()
                    .anyMatch((chatLinkEntity) -> Objects.equals(chatLinkEntity.getLinkId(), linkEntity.getLinkId())))
            .map(EntityUtils::linkEntityToLink)
            .toList();
    }
}
