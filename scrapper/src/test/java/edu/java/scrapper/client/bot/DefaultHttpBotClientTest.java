package edu.java.scrapper.client.bot;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.client.bot.DefaultHttpBotClient;
import edu.java.client.bot.HttpBotClient;
import edu.java.configuration.ClientConfigurationProperties;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.retry.LinearRetryBuilder;
import edu.java.retry.RetryBuilder;
import edu.java.scrapper.IntegrationTest;
import edu.java.utils.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.moreThanOrExactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
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
    RetryPolicy retryPolicy = new RetryPolicy(
        "linear",
        4,
        Duration.ofSeconds(1),
        "503-505"
    );
    private final ClientConfigurationProperties properties = new ClientConfigurationProperties(
        new ClientConfigurationProperties.Bot(wireMockServer.baseUrl(), retryPolicy),
        new ClientConfigurationProperties.Github(null, null),
        new ClientConfigurationProperties.StackOverflow(null, null)
    );
    private final Map<String, RetryBuilder> retryBuilderMap = Map.of(
        "linear", new LinearRetryBuilder()
    );
    private final HttpBotClient client = new DefaultHttpBotClient(properties, retryBuilderMap);

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
        //act
        String result = client.sendUpdate(request).block();
        //assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldSendUpdateAfterErrorResponse() {
        //arrange
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        String expectedResult = "Чат зарегистрирован";
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
        //act + assert
        assertThat(client.sendUpdate(request).block()).isEqualTo(expectedResult);
        wireMockServer.verify(moreThanOrExactly(2), postRequestedFor((urlEqualTo(URL))));
    }

    @Test
    void shouldGetErrorResponseAfterSpendingAllAttempts() {
        //arrange
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        wireMockServer.stubFor(post(urlEqualTo(URL))
            .willReturn(aResponse()
                .withStatus(errorStatus)
                .withHeader("Content-Type", "application/json"))
        );
        //act + assert
        assertThrows(WebClientResponseException.BadRequest.class, () -> client.sendUpdate(request).block());
    }
}
