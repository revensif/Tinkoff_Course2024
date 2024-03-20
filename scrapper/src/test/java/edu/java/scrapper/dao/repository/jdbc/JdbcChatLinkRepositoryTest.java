package edu.java.scrapper.dao.repository.jdbc;

import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcChatRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dto.ChatLink;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.database-access-type=jdbc")
@Transactional
public class JdbcChatLinkRepositoryTest extends IntegrationTest {

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final URI FIRST_URL = URI.create("link1.com");
    private static final URI SECOND_URL = URI.create("link2.com");
    private static final OffsetDateTime CURRENT_TIME = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private JdbcChatRepository chatRepository;

    @Autowired
    private JdbcLinkRepository linkRepository;

    @Autowired
    private JdbcChatLinkRepository chatLinkRepository;

    @Test
    public void shouldAddChatLinkToDatabase() {
        //arrange
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(0);
        addChatsAndLinksToDatabaseAndGetLinkIds();
        //act + assert
        chatLinkRepository.add(SECOND_ID, linkRepository.findByUri(FIRST_URL).linkId());
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(1);
        chatLinkRepository.add(FIRST_ID, linkRepository.findByUri(SECOND_URL).linkId());
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    public void shouldRemoveChatLinkFromDatabase() {
        //arrange
        long[] linkIds = prepareDatabase();
        //act + assert
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(2);
        chatLinkRepository.remove(SECOND_ID, linkIds[0]);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(1);
        chatLinkRepository.remove(FIRST_ID, linkIds[1]);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void shouldFindAllChatLinksFromDatabase() {
        //arrange
        long[] linkIds = prepareDatabase();
        //act + assert
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(2);
        assertThat(chatLinkRepository.findAll()).isEqualTo(List.of(
            new ChatLink(SECOND_ID, linkIds[0]),
            new ChatLink(FIRST_ID, linkIds[1])
        ));
    }

    @Test
    public void shouldFindChatLinkByIdsFromDatabase() {
        //arrange
        long[] linkIds = prepareDatabase();
        //act + assert
        assertThat(chatLinkRepository.findByChatAndLinkIds(SECOND_ID, linkIds[0])).isEqualTo(new ChatLink(
            SECOND_ID,
            linkIds[0]
        ));
        assertThat(chatLinkRepository.findByChatAndLinkIds(FIRST_ID, linkIds[1])).isEqualTo(new ChatLink(
            FIRST_ID,
            linkIds[1]
        ));
        assertThat(chatLinkRepository.findByChatAndLinkIds(FIRST_ID, FIRST_ID)).isNull();
    }

    @Test
    public void shouldFindAllChatsThatTrackThisLink() {
        //arrange
        long[] linkIds = prepareDatabase();
        //act + assert
        assertThat(chatLinkRepository.findAllChatsThatTrackThisLink(linkIds[0]).size()).isEqualTo(1);
        assertThat(chatLinkRepository.findAllChatsThatTrackThisLink(linkIds[0])).isEqualTo(List.of(SECOND_ID));
        assertThat(chatLinkRepository.findAllChatsThatTrackThisLink(0)).isEmpty();
    }

    @Test
    public void shouldFindAllLinksTrackedByThisChat() {
        //assert
        long[] linkIds = prepareDatabase();
        //act + assert
        assertThat(chatLinkRepository.findAllLinksTrackedByThisChat(FIRST_ID).size()).isEqualTo(1);
        assertThat(chatLinkRepository.findAllLinksTrackedByThisChat(FIRST_ID)).isEqualTo(
            List.of(linkRepository.findByUri(SECOND_URL)));
        assertThat(chatLinkRepository.findAllLinksTrackedByThisChat(0)).isEmpty();
    }

    private long[] addChatsAndLinksToDatabaseAndGetLinkIds() {
        chatRepository.add(FIRST_ID);
        chatRepository.add(SECOND_ID);
        linkRepository.add(FIRST_URL);
        linkRepository.add(SECOND_URL);
        linkRepository.changeUpdatedAt(FIRST_URL, CURRENT_TIME);
        linkRepository.changeUpdatedAt(SECOND_URL, CURRENT_TIME);
        return new long[] {linkRepository.findByUri(FIRST_URL).linkId(),
            linkRepository.findByUri(SECOND_URL).linkId()};

    }

    private long[] prepareDatabase() {
        long[] linkIds = addChatsAndLinksToDatabaseAndGetLinkIds();
        chatLinkRepository.add(SECOND_ID, linkIds[0]);
        chatLinkRepository.add(FIRST_ID, linkIds[1]);
        return linkIds;
    }
}
