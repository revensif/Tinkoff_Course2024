package edu.java.scrapper.service.jpa;

import edu.java.dao.repository.jpa.JpaChatRepository;
import edu.java.dto.Chat;
import edu.java.exception.ChatAlreadyRegisteredException;
import edu.java.exception.ChatNotFoundException;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.TgChatService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.utils.EntityUtils.chatToChatEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "app.database-access-type=jpa")
@Transactional
@DirtiesContext
public class JpaTgChatServiceTest extends IntegrationTest {

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final Chat FIRST_CHAT = new Chat(FIRST_ID);
    private static final Chat SECOND_CHAT = new Chat(SECOND_ID);

    @Autowired
    private TgChatService tgChatService;

    @Autowired
    private JpaChatRepository chatRepository;

    @Test
    public void shouldRegisterChatToDatabase() {
        //act + assert
        assertThat(tgChatService.register(FIRST_ID)).isEqualTo(FIRST_CHAT);
        assertThat(chatRepository.findAll().size()).isEqualTo(1);
        assertThat(tgChatService.register(SECOND_ID)).isEqualTo(SECOND_CHAT);
        assertThat(chatRepository.findAll().size()).isEqualTo(2);
        assertThat(chatRepository.findAll()).isEqualTo(List.of(
            chatToChatEntity(FIRST_CHAT),
            chatToChatEntity(SECOND_CHAT)
        ));
        assertThrows(ChatAlreadyRegisteredException.class, () -> tgChatService.register(FIRST_ID));
    }

    @Test
    public void shouldUnregisterChatFromDatabase() {
        //act + assert
        tgChatService.register(FIRST_ID);
        tgChatService.register(SECOND_ID);
        assertThat(chatRepository.findAll().size()).isEqualTo(2);
        assertThat(tgChatService.unregister(FIRST_ID)).isEqualTo(FIRST_CHAT);
        assertThat(chatRepository.findAll().size()).isEqualTo(1);
        assertThat(tgChatService.unregister(SECOND_ID)).isEqualTo(SECOND_CHAT);
        assertThat(chatRepository.findAll().size()).isEqualTo(0);
        assertThrows(ChatNotFoundException.class, () -> tgChatService.unregister(SECOND_ID));
    }
}
