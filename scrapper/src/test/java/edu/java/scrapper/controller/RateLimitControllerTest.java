package edu.java.scrapper.controller;

import edu.java.dto.response.LinkResponse;
import edu.java.dto.response.ListLinksResponse;
import edu.java.service.LinksService;
import edu.java.service.TgChatService;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
public class RateLimitControllerTest {

    private static final String CHAT_URL = "/tg-chat/";
    private static final String CHAT_ID_HEADER = "Tg-Chat-Id";
    private static final String LINKS_URL = "/links";
    private static final Long CHAT_ID = 1L;
    private static final ListLinksResponse RESPONSE = new ListLinksResponse(
        List.of(new LinkResponse(CHAT_ID, URI.create("link1.com"))),
        1
    );

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TgChatService chatService;

    @MockBean
    private LinksService linkService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void beforeEach() {
        Objects.requireNonNull(cacheManager.getCache("buckets")).clear();
    }

    @Test
    void shouldRegisterChatAndAfterFiveRegistrationsReturnTooManyRequestsStatus() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(CHAT_URL + CHAT_ID))
                .andExpect(status().isOk());
        }
        mockMvc.perform(post(CHAT_URL + CHAT_ID))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    void shouldAllLinksAndAfterFiveRequestsReturnTooManyRequestsStatus() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get(LINKS_URL).header(CHAT_ID_HEADER, CHAT_ID))
                .andExpect(status().isOk());
        }
        mockMvc.perform(get(LINKS_URL).header(CHAT_ID_HEADER, CHAT_ID))
            .andExpect(status().isTooManyRequests());
    }
}
