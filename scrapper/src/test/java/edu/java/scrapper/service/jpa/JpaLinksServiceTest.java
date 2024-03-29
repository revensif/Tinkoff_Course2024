package edu.java.scrapper.service.jpa;

import edu.java.dao.repository.jpa.JpaChatRepository;
import edu.java.dao.repository.jpa.JpaLinkRepository;
import edu.java.dto.Chat;
import edu.java.dto.entity.ChatEntity;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.utils.EntityUtils.chatToChatEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "app.database-access-type=jpa")
@Transactional
@DirtiesContext
public class JpaLinksServiceTest extends IntegrationTest {

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final String FIRST_URL = "https://github.com";
    private static final String SECOND_URL = "https://link1.com";
    private static final URI FIRST_LINK = URI.create(FIRST_URL);
    private static final URI SECOND_LINK = URI.create(SECOND_URL);
    private static final ChatEntity FIRST_CHAT_ENTITY = chatToChatEntity(new Chat(FIRST_ID));
    private static final ChatEntity SECOND_CHAT_ENTITY = chatToChatEntity(new Chat(SECOND_ID));

    @Autowired
    private LinksService linksService;

    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private JpaChatRepository chatRepository;

    @Test
    public void shouldAddLinkToDatabase() {
        //arrange
        AddLinkRequest request = new AddLinkRequest(FIRST_LINK);
        chatRepository.saveAndFlush(FIRST_CHAT_ENTITY);
        chatRepository.saveAndFlush(SECOND_CHAT_ENTITY);
        //act
        LinkResponse actual = linksService.add(FIRST_ID, request);
        LinkResponse expected = new LinkResponse(linkRepository.findByUrl(FIRST_URL).get().getLinkId(), FIRST_LINK);
        //assert
        assertThat(actual).isEqualTo(expected);
        assertThat(linkRepository.findAllByChatsChatId(FIRST_ID).size()).isEqualTo(1);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        linksService.add(SECOND_ID, request);
        assertThat(linkRepository.findAllByChatsChatId(FIRST_ID).size()).isEqualTo(1);
        assertThat(linkRepository.findAllByChatsChatId(SECOND_ID).size()).isEqualTo(1);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        assertThrows(LinkAlreadyTrackedException.class, () -> linksService.add(FIRST_ID, request));
    }

    @Test
    public void shouldRemoveLinkFromDatabase() {
        //arrange
        AddLinkRequest addRequest = new AddLinkRequest(FIRST_LINK);
        RemoveLinkRequest removeRequest = new RemoveLinkRequest(FIRST_LINK);
        chatRepository.saveAndFlush(FIRST_CHAT_ENTITY);
        chatRepository.saveAndFlush(SECOND_CHAT_ENTITY);
        linksService.add(FIRST_ID, addRequest);
        linksService.add(SECOND_ID, addRequest);
        //act + assert
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        LinkResponse actual = linksService.remove(FIRST_ID, removeRequest);
        LinkResponse expected = new LinkResponse(linkRepository.findByUrl(FIRST_URL).get().getLinkId(), FIRST_LINK);
        //assert
        assertThat(actual).isEqualTo(expected);
        assertThat(linkRepository.findAll().size()).isEqualTo(1);
        linksService.remove(SECOND_ID, removeRequest);
        assertThat(linkRepository.findAll().size()).isEqualTo(0);
        assertThrows(LinkNotFoundException.class, () -> linksService.remove(FIRST_ID, removeRequest));
    }

    @Test
    public void shouldListAllLinksByChatId() {
        //arrange
        AddLinkRequest firstAddRequest = new AddLinkRequest(FIRST_LINK);
        AddLinkRequest secondAddRequest = new AddLinkRequest(SECOND_LINK);
        chatRepository.saveAndFlush(FIRST_CHAT_ENTITY);
        chatRepository.saveAndFlush(SECOND_CHAT_ENTITY);

        assertThat(linksService.listAll(FIRST_ID).size()).isEqualTo(0);
        linksService.add(FIRST_ID, firstAddRequest);
        assertThat(linksService.listAll(FIRST_ID).size()).isEqualTo(1);
        linksService.add(FIRST_ID, secondAddRequest);
        //act
        ListLinksResponse actual = linksService.listAll(FIRST_ID);
        ListLinksResponse expected = new ListLinksResponse(
            List.of(
                new LinkResponse(linkRepository.findByUrl(FIRST_URL).get().getLinkId(), FIRST_LINK),
                new LinkResponse(linkRepository.findByUrl(SECOND_URL).get().getLinkId(), SECOND_LINK)
            ),
            2
        );
        //assert
        assertThat(actual).isEqualTo(expected);
        assertThat(linksService.listAll(FIRST_ID).size()).isEqualTo(2);
    }
}
