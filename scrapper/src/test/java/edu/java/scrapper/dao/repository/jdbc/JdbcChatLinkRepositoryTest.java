package edu.java.scrapper.dao.repository.jdbc;

import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcChatRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dto.ChatLink;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JdbcChatLinkRepositoryTest extends IntegrationTest {

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final URI FIRST_URL = URI.create("link1.com");
    private static final URI SECOND_URL = URI.create("link2.com");
    private static final ChatLink FIRST_CHAT_LINK = new ChatLink(FIRST_ID, SECOND_ID);
    private static final ChatLink SECOND_CHAT_LINK = new ChatLink(SECOND_ID, FIRST_ID);

    @Autowired
    private JdbcChatRepository chatRepository;

    @Autowired
    private JdbcLinkRepository linkRepository;

    @Autowired
    private JdbcChatLinkRepository chatLinkRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @Rollback
    public void shouldAddChatLinkToDatabase() {
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(0);
        addChatsAndLinksToDatabase();
        chatLinkRepository.add(FIRST_ID, SECOND_ID);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(1);
        chatLinkRepository.add(SECOND_ID, FIRST_ID);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldRemoveChatLinkFromDatabase() {
        addChatsAndLinksToDatabase();
        chatLinkRepository.add(FIRST_ID, SECOND_ID);
        chatLinkRepository.add(SECOND_ID, FIRST_ID);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(2);
        chatLinkRepository.remove(FIRST_ID, SECOND_ID);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(1);
        chatLinkRepository.remove(SECOND_ID, FIRST_ID);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllChatLinksFromDatabase() {
        addChatsAndLinksToDatabase();
        chatLinkRepository.add(FIRST_ID, SECOND_ID);
        chatLinkRepository.add(SECOND_ID, FIRST_ID);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(2);
        assertThat(chatLinkRepository.findAll()).isEqualTo(List.of(FIRST_CHAT_LINK, SECOND_CHAT_LINK));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindChatLinkByIdsFromDatabase() {
        addChatsAndLinksToDatabase();
        chatLinkRepository.add(FIRST_ID, SECOND_ID);
        chatLinkRepository.add(SECOND_ID, FIRST_ID);
        assertThat(chatLinkRepository.findByChatAndLinkIds(FIRST_ID, SECOND_ID)).isEqualTo(FIRST_CHAT_LINK);
        assertThat(chatLinkRepository.findByChatAndLinkIds(SECOND_ID, FIRST_ID)).isEqualTo(SECOND_CHAT_LINK);
        assertThat(chatLinkRepository.findByChatAndLinkIds(FIRST_ID, FIRST_ID)).isNull();
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllChatsThatTrackThisLink() {
        addChatsAndLinksToDatabase();
        chatLinkRepository.add(FIRST_ID, SECOND_ID);
        chatLinkRepository.add(SECOND_ID, FIRST_ID);
        assertThat(chatLinkRepository.findAllChatsThatTrackThisLink(FIRST_ID).size()).isEqualTo(1);
        assertThat(chatLinkRepository.findAllChatsThatTrackThisLink(FIRST_ID)).isEqualTo(List.of(SECOND_CHAT_LINK));
        assertThat(chatLinkRepository.findAllChatsThatTrackThisLink(0)).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllLinksTrackedByThisChat() {
        addChatsAndLinksToDatabase();
        chatLinkRepository.add(FIRST_ID, SECOND_ID);
        chatLinkRepository.add(SECOND_ID, FIRST_ID);
        assertThat(chatLinkRepository.findAllLinksTrackedByThisChat(FIRST_ID).size()).isEqualTo(1);
        assertThat(chatLinkRepository.findAllLinksTrackedByThisChat(FIRST_ID)).isEqualTo(List.of(FIRST_CHAT_LINK));
        assertThat(chatLinkRepository.findAllLinksTrackedByThisChat(0)).isEmpty();
    }

    private void addChatsAndLinksToDatabase() {
        chatRepository.add(FIRST_ID);
        chatRepository.add(SECOND_ID);
        linkRepository.add(FIRST_ID, FIRST_URL);
        linkRepository.add(SECOND_ID, SECOND_URL);
    }
}
