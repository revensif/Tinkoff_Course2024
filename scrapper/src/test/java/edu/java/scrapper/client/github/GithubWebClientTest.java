package edu.java.scrapper.client.github;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.client.github.GithubClient;
import edu.java.client.github.GithubWebClient;
import edu.java.dto.github.RepositoryResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"retry.backoff-type=linear", "app.use-queue=false"})
@DirtiesContext
public class GithubWebClientTest {

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

    @Autowired
    private ExchangeFilterFunction filterFunction;

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
        //arrange
        GithubClient client = new GithubWebClient(wireMockServer.baseUrl(), filterFunction);
        //act
        RepositoryResponse response = client.fetchRepository(OWNER, REPO).block();
        //assert
        assertThat(response).isEqualTo(EXPECTED_RESPONSE);
    }
}
