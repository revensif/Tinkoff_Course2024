package edu.java.scrapper.client.github;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.client.github.GithubClient;
import edu.java.client.github.GithubWebClient;
import edu.java.configuration.ClientConfigurationProperties;
import edu.java.dto.github.RepositoryResponse;
import edu.java.scrapper.IntegrationTest;
import edu.java.utils.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.moreThanOrExactly;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext
public class GithubWebClientTest extends IntegrationTest {

    private static final URI LINK_URL = URI.create("https://github.com/revensif/Tinkoff_Course2024");
    private static final String URL = "/repos/revensif/Tinkoff_Course2024";
    private static final OffsetDateTime DATE_TIME = OffsetDateTime.parse("2024-02-23T16:23:19Z");
    private static final Long REPOSITORY_ID = 182783L;
    private static final String OWNER = "revensif";
    private static final String REPO = "Tinkoff_Course2024";
    private static WireMockServer wireMockServer;
    private static final String RESPONSE_BODY = """
        {
            "id": 182783,
            "html_url": "https://github.com/revensif/Tinkoff_Course2024",
            "full_name": "revensif/Tinkoff_Course2024",
            "created_at": "2024-02-21T11:13:09Z",
            "updated_at": "2024-02-23T16:23:19Z"
        }
        """;
    private static final RepositoryResponse EXPECTED_RESPONSE = new RepositoryResponse(
        REPOSITORY_ID,
        LINK_URL,
        DATE_TIME
    );
    RetryPolicy retryPolicy = new RetryPolicy(
        "exponential",
        3,
        Duration.ofSeconds(1),
        "500-502");
    private final ClientConfigurationProperties properties = new ClientConfigurationProperties(
        new ClientConfigurationProperties.Bot(null, null),
        new ClientConfigurationProperties.Github(wireMockServer.baseUrl(), retryPolicy),
        new ClientConfigurationProperties.StackOverflow(null, null)
    );
    private final GithubClient client = new GithubWebClient(properties);

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        wireMockServer.stubFor(get(URL)
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(RESPONSE_BODY)));
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @Test
    public void shouldFetchRepository() {
        //act
        RepositoryResponse response = client.fetchRepository(OWNER, REPO).block();
        //assert
        assertThat(response).isEqualTo(EXPECTED_RESPONSE);
    }

    @Test
    void shouldGetCorrectResponseAfterErrorResponse() {
        //arrange
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        wireMockServer.stubFor(get(urlEqualTo(URL))
            .inScenario("Retry scenario")
            .whenScenarioStateIs(STARTED)
            .willSetStateTo("Retry succeeded")
            .willReturn(aResponse()
                .withStatus(errorStatus)
                .withHeader("Content-Type", "application/json")
            )
        );
        wireMockServer.stubFor(get(urlEqualTo(URL))
            .inScenario("Retry scenario")
            .whenScenarioStateIs("Retry succeeded")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(RESPONSE_BODY)
            )
        );
        //act + assert
        assertThat(client.fetchRepository(OWNER, REPO).block()).isEqualTo(EXPECTED_RESPONSE);
        wireMockServer.verify(moreThanOrExactly(2), getRequestedFor((urlEqualTo(URL))));
    }

    @Test
    void shouldGetErrorResponseAfterSpendingAllAttempts() {
        //arrange
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        wireMockServer.stubFor(get(urlEqualTo(URL))
            .willReturn(aResponse()
                .withStatus(errorStatus)
                .withHeader("Content-Type", "application/json"))
        );
        //act + assert
        assertThrows(IllegalStateException.class, () -> client.fetchRepository(OWNER, REPO).block());
        wireMockServer.verify(moreThanOrExactly(retryPolicy.maxAttempts() + 1), getRequestedFor((urlEqualTo(URL))));
    }
}
