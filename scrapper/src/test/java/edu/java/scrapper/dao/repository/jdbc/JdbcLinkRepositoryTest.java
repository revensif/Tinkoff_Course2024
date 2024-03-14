package edu.java.scrapper.dao.repository.jdbc;

import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dto.Link;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JdbcLinkRepositoryTest extends IntegrationTest {

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final URI FIRST_URL = URI.create("link1.com");
    private static final URI SECOND_URL = URI.create("link2.com");
    private static final OffsetDateTime CURRENT_TIME = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Link FIRST_LINK = new Link(FIRST_ID, FIRST_URL, CURRENT_TIME);
    private static final Link SECOND_LINK = new Link(SECOND_ID, SECOND_URL, CURRENT_TIME);

    @Autowired
    private JdbcLinkRepository linkRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @Rollback
    public void shouldAddLinkToDatabase() {
        assertThat(linkRepository.findAll().size()).isEqualTo(0);
        linkRepository.add(FIRST_ID, FIRST_URL);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        linkRepository.add(SECOND_ID, SECOND_URL);
        assertThat(linkRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldRemoveLinkFromDatabase() {
        linkRepository.add(FIRST_ID, FIRST_URL);
        linkRepository.add(SECOND_ID, SECOND_URL);
        assertThat(linkRepository.findAll().size()).isEqualTo(2);
        linkRepository.remove(FIRST_ID, FIRST_URL);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        linkRepository.remove(SECOND_ID, SECOND_URL);
        assertThat(linkRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindLinkByIdFromDatabase() {
        addAndChangeLink();
        assertThat(linkRepository.findById(FIRST_ID)).isEqualTo(FIRST_LINK);
        assertThat(linkRepository.findById(SECOND_ID)).isEqualTo(SECOND_LINK);
        assertThat(linkRepository.findById(0)).isNull();
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindLinkByUriFromDatabase() {
        addAndChangeLink();
        assertThat(linkRepository.findByUri(FIRST_URL)).isEqualTo(FIRST_LINK);
        assertThat(linkRepository.findByUri(SECOND_URL)).isEqualTo(SECOND_LINK);
        assertThat(linkRepository.findByUri(URI.create(""))).isNull();
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllLinksFromDatabase() {
        addAndChangeLink();
        assertThat(linkRepository.findAll().size()).isEqualTo(2);
        assertThat(linkRepository.findAll()).isEqualTo(List.of(FIRST_LINK, SECOND_LINK));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldChangeUpdateAtTimeInDatabase() {
        linkRepository.add(FIRST_ID, FIRST_URL);
        linkRepository.add(SECOND_ID, SECOND_URL);
        linkRepository.changeUpdatedAt(FIRST_URL, CURRENT_TIME.minusMinutes(10));
        linkRepository.changeUpdatedAt(SECOND_URL, CURRENT_TIME.plusSeconds(10));
        assertThat(linkRepository.findByUri(FIRST_URL).getUpdatedAt()).isEqualTo(CURRENT_TIME.minusMinutes(10));
        assertThat(linkRepository.findByUri(SECOND_URL).getUpdatedAt()).isEqualTo(CURRENT_TIME.plusSeconds(10));
    }

    private void addAndChangeLink() {
        linkRepository.add(FIRST_ID, FIRST_URL);
        linkRepository.add(SECOND_ID, SECOND_URL);
        linkRepository.changeUpdatedAt(FIRST_URL, CURRENT_TIME);
        linkRepository.changeUpdatedAt(SECOND_URL, CURRENT_TIME);
    }
}
