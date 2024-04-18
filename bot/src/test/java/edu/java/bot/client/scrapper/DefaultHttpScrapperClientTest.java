package edu.java.bot.client.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.utils.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.util.retry.Retry;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static edu.java.bot.utils.RetryUtils.getRetry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext
public class DefaultHttpScrapperClientTest {

    private static WireMockServer wireMockServer;
    private static final String CHAT_URL = "/tg-chat/1";
    private static final String LINKS_URL = "/links";
    private static final long ID = 1L;
    private static final URI FIRST_URI = URI.create("test1.com");
    private static final URI SECOND_URI = URI.create("test2.com");
    private final LinkResponse firstResponse = new LinkResponse(1L, FIRST_URI);
    private final LinkResponse secondResponse = new LinkResponse(2L, SECOND_URI);
    private final HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl(), Retry.max(10));

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
        //act
        String result = client.registerChat(ID).block();
        //assert
        assertThat(result).isEqualTo(expected);
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
        //act
        String result = client.deleteChat(ID).block();
        //assert
        assertThat(result).isEqualTo(expected);
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
        //act
        ListLinksResponse result = client.getAllLinks(ID).block();
        //assert
        assertThat(result).isEqualTo(expected);
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
        AddLinkRequest request = new AddLinkRequest(FIRST_URI);
        //act
        LinkResponse result = client.addLink(ID, request).block();
        //assert
        assertThat(result).isEqualTo(firstResponse);
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
        RemoveLinkRequest request = new RemoveLinkRequest(FIRST_URI);
        //act
        LinkResponse result = client.deleteLink(ID, request).block();
        //assert
        assertThat(result).isEqualTo(firstResponse);
    }

    @Test
    void shouldGetCorrectResponseAfterErrorResponse() {
        //arrange
        String testUrl = "/tg-chat/2";
        String expectedResult = "Чат зарегистрирован";
        RetryPolicy retryPolicy = new RetryPolicy("linear", 3, Duration.ofSeconds(1), "500-502");
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        wireMockServer.stubFor(post(urlEqualTo(testUrl))
            .inScenario("Retry scenario")
            .whenScenarioStateIs(STARTED)
            .willSetStateTo("Retry succeeded")
            .willReturn(aResponse()
                .withStatus(errorStatus)
                .withHeader("Content-Type", "text/plain")
            )
        );
        wireMockServer.stubFor(post(urlEqualTo(testUrl))
            .inScenario("Retry scenario")
            .whenScenarioStateIs("Retry succeeded")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/plain")
                .withBody(expectedResult)
            )
        );
        Retry retryBackoff = getRetry(retryPolicy);
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl(), retryBackoff);
        //act + assert
        assertThat(client.registerChat(2L).block()).isEqualTo(expectedResult);
        wireMockServer.verify(2, postRequestedFor((urlEqualTo(testUrl))));
    }

    @Test
    void shouldGetErrorResponseAfterSpendingAllAttempts() {
        //arrange
        String testUrl = "/tg-chat/3";
        RetryPolicy retryPolicy = new RetryPolicy("linear", 3, Duration.ofSeconds(1), "500-502");
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        wireMockServer.stubFor(post(urlEqualTo(testUrl))
            .willReturn(aResponse()
                .withStatus(errorStatus)
                .withHeader("Content-Type", "text/plain"))
        );
        Retry retryBackoff = getRetry(retryPolicy);
        HttpScrapperClient client = new DefaultHttpScrapperClient(wireMockServer.baseUrl(), retryBackoff);
        //act + assert
        assertThrows(IllegalStateException.class, () -> client.registerChat(3L).block());
        wireMockServer.verify(retryPolicy.maxAttempts() + 1, postRequestedFor((urlEqualTo(testUrl))));
    }
}
