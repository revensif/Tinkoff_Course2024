package edu.java.scrapper.dao.repository.jdbc;

import edu.java.dao.repository.ChatRepository;
import edu.java.dto.Chat;
import edu.java.scrapper.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.database-access-type=jdbc")
@Transactional
public class ChatRepositoryTest extends IntegrationTest {

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final Chat FIRST_CHAT = new Chat(FIRST_ID);
    private static final Chat SECOND_CHAT = new Chat(SECOND_ID);

    @Autowired
    private ChatRepository chatRepository;

    @Test
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
    public void shouldFindAllChatsFromDatabase() {
        chatRepository.add(FIRST_ID);
        chatRepository.add(SECOND_ID);
        assertThat(chatRepository.findAll().size()).isEqualTo(2);
        assertThat(chatRepository.findAll()).isEqualTo(List.of(FIRST_CHAT, SECOND_CHAT));
    }

    @Test
    public void shouldFindChatByIdFromDatabase() {
        chatRepository.add(FIRST_ID);
        chatRepository.add(SECOND_ID);
        assertThat(chatRepository.findById(FIRST_ID)).isEqualTo(FIRST_CHAT);
        assertThat(chatRepository.findById(SECOND_ID)).isEqualTo(SECOND_CHAT);
        assertThat(chatRepository.findById(0)).isNull();
    }
}
