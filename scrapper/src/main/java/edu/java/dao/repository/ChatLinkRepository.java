package edu.java.dao.repository;

import edu.java.dto.ChatLink;
import java.util.List;

public interface ChatLinkRepository {

    ChatLink add(long tgChatId, long linkId);

    ChatLink remove(long tgChatId, long linkId);

    List<ChatLink> findAll();

    ChatLink findByChatAndLinkIds(long tgChatId, long linkId);

    List<ChatLink> findAllChatsThatTrackThisLink(long linkId);

    List<ChatLink> findAllLinksTrackedByThisChat(long tgChatId);
}
