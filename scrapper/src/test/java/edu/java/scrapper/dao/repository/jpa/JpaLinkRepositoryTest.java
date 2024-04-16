package edu.java.scrapper.dao.repository.jpa;

import edu.java.dao.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.database-access-type=jpa")
@Transactional
public class JpaLinkRepositoryTest extends IntegrationTest {

    private static final URI FIRST_URL = URI.create("link1.com");
    private static final URI SECOND_URL = URI.create("link2.com");
    private static final OffsetDateTime CURRENT_TIME = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private JpaLinkRepository linkRepository;

    @Test
    public void shouldAddLinkToDatabase() {
        assertThat(linkRepository.findAll().size()).isEqualTo(0);
        linkRepository.add(FIRST_URL);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        linkRepository.add(SECOND_URL);
        assertThat(linkRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    public void shouldRemoveLinkFromDatabase() {
        linkRepository.add(FIRST_URL);
        linkRepository.add(SECOND_URL);
        assertThat(linkRepository.findAll().size()).isEqualTo(2);
        linkRepository.remove(FIRST_URL);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        linkRepository.remove(SECOND_URL);
        assertThat(linkRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void shouldFindLinkByUriFromDatabase() {
        addAndChangeLink();
        assertThat(linkRepository.findByUri(FIRST_URL).url()).isEqualTo(FIRST_URL);
        assertThat(linkRepository.findByUri(SECOND_URL).url()).isEqualTo(SECOND_URL);
        assertThat(linkRepository.findByUri(URI.create(""))).isNull();
    }

    @Test
    public void shouldFindAllLinksFromDatabase() {
        addAndChangeLink();
        assertThat(linkRepository.findAll().size()).isEqualTo(2);
        assertThat(linkRepository.findAll().getFirst().url()).isEqualTo(FIRST_URL);
        assertThat(linkRepository.findAll().getLast().url()).isEqualTo(SECOND_URL);
    }

    @Test
    public void shouldChangeUpdateAtTimeInDatabase() {
        linkRepository.add(FIRST_URL);
        linkRepository.add(SECOND_URL);
        linkRepository.changeUpdatedAt(FIRST_URL, CURRENT_TIME.minusMinutes(10));
        linkRepository.changeUpdatedAt(SECOND_URL, CURRENT_TIME.plusSeconds(10));
        assertThat(linkRepository.findByUri(FIRST_URL).updatedAt()).isEqualTo(CURRENT_TIME.minusMinutes(10));
        assertThat(linkRepository.findByUri(SECOND_URL).updatedAt()).isEqualTo(CURRENT_TIME.plusSeconds(10));
    }

    @Test
    public void shouldFindOutdatedLinksInDatabase() {
        linkRepository.add(FIRST_URL);
        linkRepository.add(SECOND_URL);
        linkRepository.changeUpdatedAt(FIRST_URL, CURRENT_TIME.minusMinutes(1));
        linkRepository.changeUpdatedAt(SECOND_URL, CURRENT_TIME.plusSeconds(10));
        assertThat(linkRepository.findOutdatedLinks(Duration.ofSeconds(10))).isEqualTo(List.of(linkRepository.findByUri(
            FIRST_URL)));
        assertThat(linkRepository.findOutdatedLinks(Duration.ofMinutes(2))).isEqualTo(List.of());
    }

    private void addAndChangeLink() {
        linkRepository.add(FIRST_URL);
        linkRepository.add(SECOND_URL);
        linkRepository.changeUpdatedAt(FIRST_URL, CURRENT_TIME);
        linkRepository.changeUpdatedAt(SECOND_URL, CURRENT_TIME);
    }
}
