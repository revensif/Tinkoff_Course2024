package edu.java.scrapper.dao.repository.jooq;

import edu.java.dao.repository.jooq.JooqChatRepository;
import edu.java.dto.Chat;
import edu.java.scrapper.IntegrationTest;
import java.util.List;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JooqChatRepositoryTest extends IntegrationTest {

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final Chat FIRST_CHAT = new Chat(FIRST_ID);
    private static final Chat SECOND_CHAT = new Chat(SECOND_ID);

    @Autowired
    private JooqChatRepository chatRepository;

    @Autowired
    private DSLContext dslContext;

    @Test
    @Transactional
    @Rollback
    public void shouldAddChatToDatabase() {
        assertThat(chatRepository.findAll().size()).isEqualTo(0);
        chatRepository.add(FIRST_ID);
        assertThat(chatRepository.findAll().size()).isEqualTo(1);
        chatRepository.add(SECOND_ID);
        assertThat(chatRepository.findById(FIRST_ID)).isEqualTo(FIRST_CHAT);
        assertThat(chatRepository.findById(SECOND_ID)).isEqualTo(SECOND_CHAT);
        assertThat(chatRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldRemoveChatFromDatabase() {
        chatRepository.add(FIRST_ID);
        chatRepository.add(SECOND_ID);
        assertThat(chatRepository.findAll().size()).isEqualTo(2);
        chatRepository.remove(FIRST_ID);
        assertThat(chatRepository.findAll().size()).isEqualTo(1);
        chatRepository.remove(SECOND_ID);
        assertThat(chatRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllChatsFromDatabase() {
        chatRepository.add(FIRST_ID);
        chatRepository.add(SECOND_ID);
        assertThat(chatRepository.findAll().size()).isEqualTo(2);
        assertThat(chatRepository.findAll()).isEqualTo(List.of(FIRST_CHAT, SECOND_CHAT));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindChatByIdFromDatabase() {
        chatRepository.add(FIRST_ID);
        chatRepository.add(SECOND_ID);
        assertThat(chatRepository.findById(FIRST_ID)).isEqualTo(FIRST_CHAT);
        assertThat(chatRepository.findById(SECOND_ID)).isEqualTo(SECOND_CHAT);
        assertThat(chatRepository.findById(0)).isNull();
    }
}
