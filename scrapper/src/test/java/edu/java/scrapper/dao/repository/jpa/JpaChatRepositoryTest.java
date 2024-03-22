package edu.java.scrapper.dao.repository.jpa;

import edu.java.dao.repository.jpa.JpaChatRepository;
import edu.java.dto.Chat;
import edu.java.dto.entity.ChatEntity;
import edu.java.scrapper.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.utils.EntityUtils.chatToChatEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.database-access-type=jpa")
@Transactional
public class JpaChatRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaChatRepository chatRepository;

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final ChatEntity FIRST_CHAT_ENTITY = chatToChatEntity(new Chat(FIRST_ID));
    private static final ChatEntity SECOND_CHAT_ENTITY = chatToChatEntity(new Chat(SECOND_ID));

    @Test
    public void shouldAddChatToDatabase() {
        assertThat(chatRepository.findAll().size()).isEqualTo(0);
        chatRepository.saveAndFlush(FIRST_CHAT_ENTITY);
        assertThat(chatRepository.findAll().size()).isEqualTo(1);
        chatRepository.saveAndFlush(SECOND_CHAT_ENTITY);
        assertThat(chatRepository.findById(FIRST_ID).get()).isEqualTo(FIRST_CHAT_ENTITY);
        assertThat(chatRepository.findById(SECOND_ID).get()).isEqualTo(SECOND_CHAT_ENTITY);
        assertThat(chatRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    public void shouldRemoveChatFromDatabase() {
        chatRepository.saveAndFlush(FIRST_CHAT_ENTITY);
        chatRepository.saveAndFlush(SECOND_CHAT_ENTITY);
        assertThat(chatRepository.findAll().size()).isEqualTo(2);
        chatRepository.delete(FIRST_CHAT_ENTITY);
        assertThat(chatRepository.findAll().size()).isEqualTo(1);
        chatRepository.delete(SECOND_CHAT_ENTITY);
        assertThat(chatRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void shouldFindAllChatsFromDatabase() {
        chatRepository.saveAndFlush(FIRST_CHAT_ENTITY);
        chatRepository.saveAndFlush(SECOND_CHAT_ENTITY);
        assertThat(chatRepository.findAll().size()).isEqualTo(2);
        assertThat(chatRepository.findAll()).isEqualTo(List.of(FIRST_CHAT_ENTITY, SECOND_CHAT_ENTITY));
    }

    @Test
    public void shouldFindChatByIdFromDatabase() {
        chatRepository.saveAndFlush(FIRST_CHAT_ENTITY);
        chatRepository.saveAndFlush(SECOND_CHAT_ENTITY);
        assertThat(chatRepository.findById(FIRST_ID).get()).isEqualTo(FIRST_CHAT_ENTITY);
        assertThat(chatRepository.findById(SECOND_ID).get()).isEqualTo(SECOND_CHAT_ENTITY);
        assertThat(chatRepository.findById(0L)).isEmpty();
    }
}
