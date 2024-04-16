package edu.java.scrapper.service.jdbc;

import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcChatRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dto.request.AddLinkRequest;
import edu.java.dto.request.RemoveLinkRequest;
import edu.java.dto.response.LinkResponse;
import edu.java.dto.response.ListLinksResponse;
import edu.java.exception.LinkAlreadyTrackedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.LinksService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "app.database-access-type=jdbc")
@Transactional
public class JdbcLinksServiceTest extends IntegrationTest {

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final URI FIRST_URL = URI.create("https://github.com");
    private static final URI SECOND_URL = URI.create("https://link1.com");

    @Autowired
    private LinksService linksService;

    @Autowired
    private JdbcChatLinkRepository chatLinkRepository;

    @Autowired
    private JdbcLinkRepository linkRepository;

    @Autowired
    private JdbcChatRepository chatRepository;

    @Test
    public void shouldAddLinkToDatabase() {
        //arrange
        AddLinkRequest request = new AddLinkRequest(FIRST_URL);
        chatRepository.add(FIRST_ID);
        chatRepository.add(SECOND_ID);
        //act
        LinkResponse actual = linksService.add(FIRST_ID, request);
        LinkResponse expected = new LinkResponse(linkRepository.findByUri(FIRST_URL).linkId(), FIRST_URL);
        //assert
        assertThat(actual).isEqualTo(expected);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(1);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        linksService.add(SECOND_ID, request);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(2);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        assertThrows(LinkAlreadyTrackedException.class, () -> linksService.add(FIRST_ID, request));
    }

    @Test
    public void shouldRemoveLinkFromDatabase() {
        //arrange
        AddLinkRequest addRequest = new AddLinkRequest(FIRST_URL);
        RemoveLinkRequest removeRequest = new RemoveLinkRequest(FIRST_URL);
        chatRepository.add(FIRST_ID);
        chatRepository.add(SECOND_ID);
        linksService.add(FIRST_ID, addRequest);
        linksService.add(SECOND_ID, addRequest);
        //act + assert
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(2);
        LinkResponse actual = linksService.remove(FIRST_ID, removeRequest);
        LinkResponse expected = new LinkResponse(linkRepository.findByUri(FIRST_URL).linkId(), FIRST_URL);
        //assert
        assertThat(actual).isEqualTo(expected);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(1);
        linksService.remove(SECOND_ID, removeRequest);
        assertThat(linkRepository.findAll().size()).isEqualTo(0);
        assertThat(chatLinkRepository.findAll().size()).isEqualTo(0);
        assertThrows(LinkNotFoundException.class, () -> linksService.remove(FIRST_ID, removeRequest));
    }

    @Test
    public void shouldListAllLinksByChatId() {
        //arrange
        AddLinkRequest firstAddRequest = new AddLinkRequest(FIRST_URL);
        AddLinkRequest secondAddRequest = new AddLinkRequest(SECOND_URL);
        chatRepository.add(FIRST_ID);
        chatRepository.add(SECOND_ID);

        assertThat(linksService.listAll(FIRST_ID).size()).isEqualTo(0);
        linksService.add(FIRST_ID, firstAddRequest);
        assertThat(linksService.listAll(FIRST_ID).size()).isEqualTo(1);
        linksService.add(FIRST_ID, secondAddRequest);
        //act
        ListLinksResponse actual = linksService.listAll(FIRST_ID);
        ListLinksResponse expected = new ListLinksResponse(
            List.of(
                new LinkResponse(linkRepository.findByUri(FIRST_URL).linkId(), FIRST_URL),
                new LinkResponse(linkRepository.findByUri(SECOND_URL).linkId(), SECOND_URL)
            ),
            2
        );
        //assert
        assertThat(actual).isEqualTo(expected);
        assertThat(linksService.listAll(FIRST_ID).size()).isEqualTo(2);
    }
}
