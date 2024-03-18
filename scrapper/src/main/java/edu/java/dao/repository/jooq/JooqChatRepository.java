package edu.java.dao.repository.jooq;

import edu.java.dao.repository.ChatRepository;
import edu.java.dto.Chat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.dao.jooq.Tables.CHAT;

@Repository
@RequiredArgsConstructor
public class JooqChatRepository implements ChatRepository {

    private final DSLContext dslContext;

    @Override
    public Chat add(long tgChatId) {
        dslContext.insertInto(CHAT)
            .set(CHAT.CHAT_ID, tgChatId)
            .execute();
        return findById(tgChatId);
    }

    @Override
    public Chat remove(long tgChatId) {
        Chat removedChat = findById(tgChatId);
        dslContext.deleteFrom(CHAT)
            .where(CHAT.CHAT_ID.eq(tgChatId))
            .execute();
        return removedChat;
    }

    @Override
    public List<Chat> findAll() {
        return dslContext.select(CHAT.fields())
            .from(CHAT)
            .fetchInto(Chat.class);
    }

    @Override
    public Chat findById(long tgChatId) {
        return dslContext.select(CHAT.fields())
            .from(CHAT)
            .where(CHAT.CHAT_ID.eq(tgChatId))
            .fetchOneInto(Chat.class);
    }
}
