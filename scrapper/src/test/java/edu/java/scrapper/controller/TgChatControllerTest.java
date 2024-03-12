package edu.java.scrapper.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import edu.java.controller.TgChatController;
import edu.java.exception.ChatAlreadyRegisteredException;
import edu.java.exception.ChatNotFoundException;
import edu.java.service.TgChatService;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TgChatController.class)
@AutoConfigureMockMvc
public class TgChatControllerTest {

    private static final String URL = "/tg-chat/1";
    private static final Long CHAT_ID = 1L;
    private static final URI LINK_URL = URI.create("link1.com");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TgChatService tgChatService;

    @BeforeEach
    public void beforeEach() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Test
    void shouldRegisterChatAndReturnOkStatus() throws Exception {
        mockMvc.perform(post(URL))
            .andExpect(status().isOk())
            .andExpect(content().string("Чат зарегистрирован"));
    }

    @Test
    void shouldUnregisterChatAndReturnOkStatus() throws Exception {
        mockMvc.perform(delete(URL))
            .andExpect(status().isOk())
            .andExpect(content().string("Чат успешно удален"));
    }

    @Test
    void shouldRegisterChatAndReturnConflictStatus() throws Exception {
        //arrange
        when(tgChatService.registerChat()).thenThrow(ChatAlreadyRegisteredException.class);
        //act + assert
        mockMvc.perform(post(URL))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.description").value("Чат уже был зарегистрирован"));
    }

    @Test
    void shouldUnregisterChatAndReturnNotFoundStatus() throws Exception {
        //arrange
        when(tgChatService.registerChat()).thenThrow(ChatNotFoundException.class);
        //act + assert
        mockMvc.perform(post(URL))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.description").value("Чат не существует"));
    }
}
