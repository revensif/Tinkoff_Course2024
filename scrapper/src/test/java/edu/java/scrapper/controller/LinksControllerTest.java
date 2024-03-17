package edu.java.scrapper.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import edu.java.controller.LinksController;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinksController.class)
@AutoConfigureMockMvc
public class LinksControllerTest extends IntegrationTest {

    private static final String HEADER = "Tg-Chat-Id";
    private static final String URL = "/links";
    private static final Long CHAT_ID = 1L;
    private static final URI LINK_URL = URI.create("link1.com");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinksService linksService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Test
    void shouldGetAllLinksAndReturnOkStatus() throws Exception {
        //assert
        ListLinksResponse expected = new ListLinksResponse(
            List.of(new LinkResponse(CHAT_ID, LINK_URL)),
            1
        );
        when(linksService.listAll(CHAT_ID)).thenReturn(expected);
        //act + arrange
        mockMvc.perform(get(URL)
                .header(HEADER, CHAT_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links[0].id").value(CHAT_ID))
            .andExpect(jsonPath("$.links[0].url").value(LINK_URL.toString()))
            .andExpect(jsonPath("$.size").value(1));
    }

    @Test
    void shouldAddLinkAndReturnOkStatus() throws Exception {
        //arrange
        LinkResponse expected = new LinkResponse(CHAT_ID, LINK_URL);
        AddLinkRequest addLinkRequest = new AddLinkRequest(LINK_URL);
        String requestJson = objectMapper.writeValueAsString(addLinkRequest);
        when(linksService.add(CHAT_ID, addLinkRequest)).thenReturn(expected);
        //act + assert
        mockMvc.perform(post(URL)
                .header(HEADER, CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(CHAT_ID))
            .andExpect(jsonPath("$.url").value(LINK_URL.toString()));
    }

    @Test
    void shouldRemoveLinkAndReturnOkStatus() throws Exception {
        //arrange
        LinkResponse expected = new LinkResponse(CHAT_ID, LINK_URL);
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(LINK_URL);
        String requestJson = objectMapper.writeValueAsString(removeLinkRequest);
        when(linksService.remove(CHAT_ID, removeLinkRequest)).thenReturn(expected);
        //act + assert
        mockMvc.perform(delete(URL)
                .header(HEADER, CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(CHAT_ID))
            .andExpect(jsonPath("$.url").value(LINK_URL.toString()));
    }

    @Test
    void shouldAddLinkAndReturnConflictStatus() throws Exception {
        //arrange
        AddLinkRequest addLinkRequest = new AddLinkRequest(LINK_URL);
        String requestJson = objectMapper.writeValueAsString(addLinkRequest);
        when(linksService.add(CHAT_ID, addLinkRequest)).thenThrow(LinkAlreadyTrackedException.class);
        //act + assert
        mockMvc.perform(post(URL)
                .header(HEADER, CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.description").value("Ссылка уже отслеживается"));
    }

    @Test
    void shouldDeleteLinkAndReturnNotFoundStatus() throws Exception {
        //arrange
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(LINK_URL);
        String requestJson = objectMapper.writeValueAsString(removeLinkRequest);
        when(linksService.remove(CHAT_ID, removeLinkRequest)).thenThrow(LinkNotFoundException.class);
        //act + assert
        mockMvc.perform(delete(URL)
                .header(HEADER, CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.description").value("Ссылка не существует"));
    }

    @Test
    void shouldDeleteLinkAndReturnBadRequestStatus() throws Exception {
        mockMvc.perform(delete(URL)
                .header(HEADER, CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "url": null
                    }
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"));
    }
}
