package edu.java.bot.client.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultHttpScrapperClientTest {

    private static WireMockServer wireMockServer;
    private static final String CHAT_URL = "/tg-chat/1";
    private static final String LINKS_URL = "/links";
    private static final long ID = 1L;
    private static final String GITHUB = "https://github.com/revensif/Tinkoff_Course2024";
    private final LinkResponse firstResponse = new LinkResponse(1L, URI.create("test1.com"));
    private final LinkResponse secondResponse = new LinkResponse(2L, URI.create("test2.com"));

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @Test
    public void shouldRegisterChat() {
        //arrange
        String expected = "Чат зарегестрирован";
        wireMockServer.stubFor(post(CHAT_URL)
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody(expected)
            ));
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl());
        //act
        String result = client.registerChat(ID).block();
        //assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void shouldRegisterChatAndThrowException() {
        //arrange
        wireMockServer.stubFor(post(CHAT_URL)
            .willReturn(aResponse()
                .withStatus(400)
            ));
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl());
        //act + assert
        assertThrows(WebClientResponseException.BadRequest.class, () -> client.registerChat(ID).block());
    }

    @Test
    public void shouldDeleteChat() {
        //arrange
        String expected = "Чат успешно удален";
        wireMockServer.stubFor(delete(CHAT_URL)
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody(expected)
            ));
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl());
        //act
        String result = client.deleteChat(ID).block();
        //assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void shouldDeleteChatAndThrowException() {
        //arrange
        wireMockServer.stubFor(delete(CHAT_URL)
            .willReturn(aResponse()
                .withStatus(400)
            ));
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl());
        //act + assert
        assertThrows(WebClientResponseException.BadRequest.class, () -> client.deleteChat(ID).block());
    }

    @Test
    public void shouldGetAllLinks() {
        //arrange
        ListLinksResponse expected = new ListLinksResponse(
            List.of(firstResponse, secondResponse),
            2
        );
        wireMockServer.stubFor(get(LINKS_URL)
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody("""
                    {
                        "links": [
                            {
                                "id": 1,
                                "url": "test1.com"
                            },
                            {
                                "id": 2,
                                "url": "test2.com"
                            }
                        ],
                        "size": 2
                    }
                    """)
            ));
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl());
        //act
        ListLinksResponse result = client.getAllLinks(ID).block();
        //assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void shouldGetAllLinksAndThrowException() {
        //arrange
        wireMockServer.stubFor(get(LINKS_URL)
            .willReturn(aResponse()
                .withStatus(400)
            ));
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl());
        //act + assert
        assertThrows(WebClientResponseException.BadRequest.class, () -> client.getAllLinks(ID).block());
    }

    @Test
    public void shouldAddLink() {
        //arrange
        wireMockServer.stubFor(post(LINKS_URL)
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody("""
                    {
                        "id": 1,
                        "url": "test1.com"
                    }
                    """)
            ));
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl());
        AddLinkRequest request = new AddLinkRequest("test1.com");
        //act
        LinkResponse result = client.addLink(ID, request).block();
        //assert
        assertThat(result).isEqualTo(firstResponse);
    }

    @Test
    public void shouldAddLinkAndThrowException() {
        //arrange
        wireMockServer.stubFor(post(LINKS_URL)
            .willReturn(aResponse()
                .withStatus(400)
            ));
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl());
        AddLinkRequest request = new AddLinkRequest("test1.com");
        //act + assert
        assertThrows(WebClientResponseException.BadRequest.class, () -> client.addLink(ID, request).block());
    }

    @Test
    public void shouldDeleteLink() {
        //arrange
        wireMockServer.stubFor(delete(LINKS_URL)
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody("""
                    {
                        "id": 1,
                        "url": "test1.com"
                    }
                    """)
            ));
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl());
        RemoveLinkRequest request = new RemoveLinkRequest("test1.com");
        //act
        LinkResponse result = client.deleteLink(ID, request).block();
        //assert
        assertThat(result).isEqualTo(firstResponse);
    }

    @Test
    public void shouldDeleteLinkAndThrowException() {
        //arrange
        wireMockServer.stubFor(delete(LINKS_URL)
            .willReturn(aResponse()
                .withStatus(400)
            ));
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl());
        RemoveLinkRequest request = new RemoveLinkRequest("test1.com");
        //act + assert
        assertThrows(WebClientResponseException.BadRequest.class, () -> client.deleteLink(ID, request).block());
    }
}
