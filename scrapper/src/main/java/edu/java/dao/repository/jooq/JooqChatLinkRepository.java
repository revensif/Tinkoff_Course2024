package edu.java.dao.repository.jooq;

import edu.java.dao.repository.ChatLinkRepository;
import edu.java.dto.ChatLink;
import edu.java.dto.Link;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.dao.jooq.Tables.CHAT_LINK;
import static edu.java.dao.jooq.Tables.LINK;

@Repository
@RequiredArgsConstructor
public class JooqChatLinkRepository implements ChatLinkRepository {

    private final DSLContext dslContext;

    @Override
    public ChatLink add(long tgChatId, long linkId) {
        dslContext.insertInto(CHAT_LINK)
            .set(CHAT_LINK.CHAT_ID, tgChatId)
            .set(CHAT_LINK.LINK_ID, linkId)
            .execute();
        return findByChatAndLinkIds(tgChatId, linkId);
    }

    @Override
    public ChatLink remove(long tgChatId, long linkId) {
        ChatLink removedChatLink = findByChatAndLinkIds(tgChatId, linkId);
        dslContext.deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(tgChatId).and(CHAT_LINK.LINK_ID.eq(linkId)))
            .execute();
        return removedChatLink;
    }

    @Override
    public List<ChatLink> findAll() {
        return dslContext.select(CHAT_LINK.fields())
            .from(CHAT_LINK)
            .fetchInto(ChatLink.class);
    }

    @Override
    public ChatLink findByChatAndLinkIds(long tgChatId, long linkId) {
        return dslContext.select(CHAT_LINK.fields())
            .from(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(tgChatId).and(CHAT_LINK.LINK_ID.eq(linkId)))
            .fetchOneInto(ChatLink.class);
    }

    @Override
    public List<Long> findAllChatsThatTrackThisLink(long linkId) {
        return dslContext.select(CHAT_LINK.CHAT_ID)
            .from(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.eq(linkId))
            .fetchInto(Long.class);
    }

    @Override
    public List<Link> findAllLinksTrackedByThisChat(long tgChatId) {
        return dslContext.select(LINK.fields())
            .from(LINK)
            .join(CHAT_LINK).on(LINK.LINK_ID.eq(CHAT_LINK.LINK_ID))
            .where(CHAT_LINK.CHAT_ID.eq(tgChatId))
            .fetchInto(Link.class);
    }
}
