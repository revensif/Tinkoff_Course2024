package edu.java.scrapper.client.bot;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.client.bot.DefaultHttpBotClient;
import edu.java.client.bot.HttpBotClient;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.scrapper.IntegrationTest;
import edu.java.utils.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.util.retry.Retry;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.moreThanOrExactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static edu.java.utils.RetryUtils.getRetry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext
public class DefaultHttpBotClientTest extends IntegrationTest {

    private static WireMockServer wireMockServer;
    private static final String URL = "/updates";
    private static final URI GITHUB = URI.create("https://github.com/revensif/Tinkoff_Course2024");
    private final LinkUpdateRequest request = new LinkUpdateRequest(
        1L,
        GITHUB,
        "Empty",
        new ArrayList<>()
    );

    private final Retry retryBackoff = Retry.max(10);

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
        HttpBotClient client = new DefaultHttpBotClient(wireMockServer.baseUrl(), retryBackoff);
        //act
        String result = client.sendUpdate(request).block();
        //assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldSendUpdateAfterErrorResponse() {
        //arrange
        String expectedResult = "Чат зарегистрирован";
        RetryPolicy retryPolicy = new RetryPolicy("constant", 3, Duration.ofSeconds(1), "500-502");
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        wireMockServer.stubFor(post(urlEqualTo(URL))
            .inScenario("Retry scenario")
            .whenScenarioStateIs(STARTED)
            .willSetStateTo("Retry succeeded")
            .willReturn(aResponse()
                .withStatus(errorStatus)
                .withHeader("Content-Type", "application/json")
            )
        );
        wireMockServer.stubFor(post(urlEqualTo(URL))
            .inScenario("Retry scenario")
            .whenScenarioStateIs("Retry succeeded")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(expectedResult)
            )
        );
        Retry retryBackoff = getRetry(retryPolicy);
        HttpBotClient client = new DefaultHttpBotClient(wireMockServer.baseUrl(), retryBackoff);
        //act + assert
        assertThat(client.sendUpdate(request).block()).isEqualTo(expectedResult);
        wireMockServer.verify(moreThanOrExactly(2), postRequestedFor((urlEqualTo(URL))));
    }

    @Test
    void shouldGetErrorResponseAfterSpendingAllAttempts() {
        //arrange
        RetryPolicy retryPolicy = new RetryPolicy("exponential", 3, Duration.ofSeconds(1), "500-502");
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        wireMockServer.stubFor(post(urlEqualTo(URL))
            .willReturn(aResponse()
                .withStatus(errorStatus)
                .withHeader("Content-Type", "application/json"))
        );
        Retry retryBackoff = getRetry(retryPolicy);
        HttpBotClient client = new DefaultHttpBotClient(wireMockServer.baseUrl(), retryBackoff);
        //act + assert
        assertThrows(IllegalStateException.class, () -> client.sendUpdate(request).block());
        wireMockServer.verify(moreThanOrExactly(retryPolicy.maxAttempts() + 1), postRequestedFor((urlEqualTo(URL))));
    }
}
