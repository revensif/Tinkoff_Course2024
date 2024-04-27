package edu.java.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.dto.request.LinkUpdateRequest;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
public class RateLimitControllerTest {

    private static final String URL = "/updates";

    private static final LinkUpdateRequest UPDATE_REQUEST = new LinkUpdateRequest(
        1L,
        URI.create("link1.com"),
        "description",
        List.of(1L, 2L)
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void beforeEach() {
        Objects.requireNonNull(cacheManager.getCache("buckets")).clear();
    }

    @Test
    void shouldSendUpdatesAndAfterFiveUpdatesReturnTooManyRequestsStatus() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(UPDATE_REQUEST);
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .andExpect(status().isOk());
        }
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isTooManyRequests());
    }
}
