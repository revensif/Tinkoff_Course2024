package edu.java.scrapper.dao.repository.jpa;

import edu.java.dao.repository.jpa.JpaLinkRepository;
import edu.java.dto.Link;
import edu.java.dto.entity.LinkEntity;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import edu.java.utils.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.utils.EntityUtils.createLinkEntity;
import static edu.java.utils.EntityUtils.linkEntityToLink;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.database-access-type=jpa")
@Transactional
@DirtiesContext
public class JpaLinkRepositoryTest extends IntegrationTest {

    private static final String FIRST_URL = "link1.com";
    private static final String SECOND_URL = "link2.com";
    private static final OffsetDateTime CURRENT_TIME = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    private static final LinkEntity FIRST_LINK_ENTITY = createLinkEntity(FIRST_URL, CURRENT_TIME);
    private static final LinkEntity SECOND_LINK_ENTITY = createLinkEntity(SECOND_URL, CURRENT_TIME);

    @Autowired
    private JpaLinkRepository linkRepository;

    @Test
    public void shouldAddLinkToDatabase() {
        assertThat(linkRepository.findAll().size()).isEqualTo(0);
        linkRepository.saveAndFlush(FIRST_LINK_ENTITY);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        linkRepository.saveAndFlush(SECOND_LINK_ENTITY);
        assertThat(linkRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    public void shouldRemoveLinkFromDatabase() {
        linkRepository.saveAndFlush(FIRST_LINK_ENTITY);
        linkRepository.saveAndFlush(SECOND_LINK_ENTITY);
        assertThat(linkRepository.findAll().size()).isEqualTo(2);
        linkRepository.delete(FIRST_LINK_ENTITY);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        linkRepository.delete(SECOND_LINK_ENTITY);
        assertThat(linkRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void shouldFindLinkByUriFromDatabase() {
        addAndChangeLink();
        assertThat(linkRepository.findByUrl(FIRST_URL).get().getUrl()).isEqualTo(FIRST_URL);
        assertThat(linkRepository.findByUrl(SECOND_URL).get().getUrl()).isEqualTo(SECOND_URL);
        assertThat(linkRepository.findByUrl(URI.create("").toString())).isEmpty();
    }

    @Test
    public void shouldFindAllLinksFromDatabase() {
        addAndChangeLink();
        assertThat(linkRepository.findAll().size()).isEqualTo(2);
        assertThat(linkRepository.findAll().getFirst().getUrl()).isEqualTo(FIRST_URL);
        assertThat(linkRepository.findAll().getLast().getUrl()).isEqualTo(SECOND_URL);
    }

    @Test
    public void shouldChangeUpdateAtTimeInDatabase() {
        linkRepository.saveAndFlush(FIRST_LINK_ENTITY);
        linkRepository.saveAndFlush(SECOND_LINK_ENTITY);
        linkRepository.changeUpdatedAt(FIRST_URL, CURRENT_TIME.minusMinutes(10));
        linkRepository.changeUpdatedAt(SECOND_URL, CURRENT_TIME.plusSeconds(10));
        assertThat(linkRepository.findByUrl(FIRST_URL).get()
            .getUpdatedAt()).isEqualTo(CURRENT_TIME.minusMinutes(10));
        assertThat(linkRepository.findByUrl(SECOND_URL).get()
            .getUpdatedAt()).isEqualTo(CURRENT_TIME.plusSeconds(10));
    }

    @Test
    public void shouldFindOutdatedLinksInDatabase() {
        linkRepository.saveAndFlush(FIRST_LINK_ENTITY);
        linkRepository.saveAndFlush(SECOND_LINK_ENTITY);
        linkRepository.changeUpdatedAt(FIRST_URL, CURRENT_TIME.minusMinutes(1));
        linkRepository.changeUpdatedAt(SECOND_URL, CURRENT_TIME.plusSeconds(10));
        Link expectedLink = linkEntityToLink(linkRepository.findByUrl(FIRST_URL).get());
        assertThat(linkRepository.findOutdatedLinks(CURRENT_TIME.plusSeconds(5))
            .stream()
            .map(EntityUtils::linkEntityToLink)).isEqualTo(List.of(expectedLink));
        assertThat(linkRepository.findOutdatedLinks(CURRENT_TIME.minusMinutes(2))).isEqualTo(List.of());
    }

    private void addAndChangeLink() {
        linkRepository.saveAndFlush(FIRST_LINK_ENTITY);
        linkRepository.saveAndFlush(SECOND_LINK_ENTITY);
        linkRepository.changeUpdatedAt(FIRST_URL, CURRENT_TIME);
        linkRepository.changeUpdatedAt(SECOND_URL, CURRENT_TIME);
    }
}
