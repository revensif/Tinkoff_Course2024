package edu.java.bot.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.BotService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UpdateController.class)
@DirtiesContext
@AutoConfigureMockMvc
public class UpdateControllerTest {

    private static final String URL = "/updates";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BotService botService;

    @Test
    public void shouldSendUpdateAndReturnOkStatus() throws Exception {
        //arrange
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(
            1L,
            URI.create("link1.com"),
            "description",
            List.of(1L, 2L, 3L)
        );
        String requestJson = objectMapper.writeValueAsString(linkUpdateRequest);
        //act + assert
        mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
        ).andExpect(status().isOk());
    }

    @Test
    public void shouldSendUpdateAndReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": 123,
                      "url": null,
                      "description": "description",
                      "tgChatIds": [1]
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"));
    }
}
