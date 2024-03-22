package edu.java.dao.repository;

import edu.java.dto.ChatLink;
import edu.java.dto.Link;
import java.util.List;

public interface ChatLinkRepository {

    ChatLink add(long tgChatId, long linkId);

    ChatLink remove(long tgChatId, long linkId);

    List<ChatLink> findAll();

    ChatLink findByChatAndLinkIds(long tgChatId, long linkId);

    List<Long> findAllChatsThatTrackThisLink(long linkId);

    List<Link> findAllLinksTrackedByThisChat(long tgChatId);
}
