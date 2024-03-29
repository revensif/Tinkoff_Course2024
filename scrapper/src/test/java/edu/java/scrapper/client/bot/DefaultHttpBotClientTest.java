package edu.java.scrapper.client.bot;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.client.bot.DefaultHttpBotClient;
import edu.java.client.bot.HttpBotClient;
import edu.java.configuration.retry.RetryBackoffConfigurationProperties;
import edu.java.dto.request.LinkUpdateRequest;
import java.net.URI;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "retry.backoff-type=constant")
@DirtiesContext
public class DefaultHttpBotClientTest {

    private static WireMockServer wireMockServer;
    private static final String URL = "/updates";
    private static final URI GITHUB = URI.create("https://github.com/revensif/Tinkoff_Course2024");
    private final LinkUpdateRequest request = new LinkUpdateRequest(
        1L,
        GITHUB,
        "Empty",
        new ArrayList<>()
    );

    @Autowired
    private RetryBackoffConfigurationProperties configurationProperties;

    @Autowired
    private ExchangeFilterFunction filterFunction;

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
    public void shouldSendUpdate() {
        //arrange
        String expected = "Обновление обработано";
        wireMockServer.stubFor(post(URL)
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-type", "application/json")
                .withBody(expected)
            ));
        HttpBotClient client = new DefaultHttpBotClient(wireMockServer.baseUrl(), filterFunction);
        //act
        String result = client.sendUpdate(request).block();
        //assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void shouldSendUpdateAndThrowException() {
        //arrange
        wireMockServer.stubFor(post(URL)
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-type", "application/json")
            ));
        HttpBotClient client = new DefaultHttpBotClient(wireMockServer.baseUrl(), filterFunction);
        //act + assert
        assertThrows(WebClientResponseException.BadRequest.class, () -> client.sendUpdate(request).block());
    }
}
