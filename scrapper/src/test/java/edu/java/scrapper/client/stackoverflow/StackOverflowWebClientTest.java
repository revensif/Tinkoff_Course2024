package edu.java.scrapper.client.stackoverflow;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.client.stackoverflow.StackOverflowWebClient;
import edu.java.configuration.ClientConfigurationProperties;
import edu.java.dto.stackoverflow.CommentsResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.scrapper.IntegrationTest;
import edu.java.utils.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
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
import static edu.java.scrapper.client.stackoverflow.StackOverflowJsonResponse.COMMENTS_RESPONSE_BODY;
import static edu.java.scrapper.client.stackoverflow.StackOverflowJsonResponse.QUESTION_RESPONSE_BODY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext
public class StackOverflowWebClientTest extends IntegrationTest {

    private static final URI LINK_URL = URI.create("https://stackoverflow.com/questions/12345/test_for_hw2");
    private static final String QUESTION_URL = "/questions/12345?site=stackoverflow";
    private static final String COMMENTS_URL = "/questions/12345/comments?site=stackoverflow";
    private static final Long QUESTION_ID = 12345L;
    private static final Long FIRST_COMMENT_ID = 110156174L;
    private static final Long SECOND_COMMENT_ID = 96880783L;
    private static final Integer ANSWER_COUNT = 17;
    private static final OffsetDateTime DATE_TIME =
        OffsetDateTime.ofInstant(Instant.ofEpochSecond(1708698398L), ZoneOffset.UTC);
    private static WireMockServer wireMockServer;
    private static final QuestionResponse QUESTION_EXPECTED_RESPONSE = new QuestionResponse(
        List.of(
            new QuestionResponse.ItemResponse(
                QUESTION_ID,
                LINK_URL,
                ANSWER_COUNT,
                DATE_TIME
            )
        )
    );
    private static final CommentsResponse COMMENTS_EXPECTED_RESPONSE = new CommentsResponse(
        List.of(
            new CommentsResponse.ItemResponse(FIRST_COMMENT_ID),
            new CommentsResponse.ItemResponse(SECOND_COMMENT_ID)
        )
    );
    RetryPolicy retryPolicy = new RetryPolicy(
        "constant",
        5,
        Duration.ofSeconds(1),
        "502-502"
    );
    private final ClientConfigurationProperties properties = new ClientConfigurationProperties(
        new ClientConfigurationProperties.Bot(null, null),
        new ClientConfigurationProperties.Github(null, null),
        new ClientConfigurationProperties.StackOverflow(wireMockServer.baseUrl(), retryPolicy)
    );
    private final StackOverflowClient client = new StackOverflowWebClient(properties);

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        wireMockServer.stubFor(get(QUESTION_URL)
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(QUESTION_RESPONSE_BODY)));
        wireMockServer.stubFor(get(COMMENTS_URL)
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(COMMENTS_RESPONSE_BODY)));
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @Test
    public void shouldFetchQuestion() {
        //act
        QuestionResponse response = client.fetchQuestion(QUESTION_ID).block();
        //assert
        assertThat(response).isEqualTo(QUESTION_EXPECTED_RESPONSE);
    }

    @Test
    public void shouldFetchComments() {
        //act
        CommentsResponse response = client.fetchComments(QUESTION_ID).block();
        //assert
        assertThat(response).isEqualTo(COMMENTS_EXPECTED_RESPONSE);
    }

    @Test
    void shouldGetCorrectResponseAfterFetchingCommentsAfterErrorResponse() {
        //arrange
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        wireMockServer.stubFor(get(urlEqualTo(COMMENTS_URL))
            .inScenario("Retry scenario")
            .whenScenarioStateIs(STARTED)
            .willSetStateTo("Retry succeeded")
            .willReturn(aResponse()
                .withStatus(errorStatus)
                .withHeader("Content-Type", "application/json")
            )
        );
        wireMockServer.stubFor(get(urlEqualTo(COMMENTS_URL))
            .inScenario("Retry scenario")
            .whenScenarioStateIs("Retry succeeded")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(COMMENTS_RESPONSE_BODY)
            )
        );
        //act + assert
        assertThat(client.fetchComments(QUESTION_ID).block()).isEqualTo(COMMENTS_EXPECTED_RESPONSE);
        wireMockServer.verify(moreThanOrExactly(2), getRequestedFor((urlEqualTo(COMMENTS_URL))));
    }

    @Test
    void shouldGetErrorResponseAfterFetchingCommentsAfterSpendingAllAttempts() {
        //arrange
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        wireMockServer.stubFor(get(urlEqualTo(COMMENTS_URL))
            .willReturn(aResponse()
                .withStatus(errorStatus)
                .withHeader("Content-Type", "application/json"))
        );
        //act + assert
        assertThrows(IllegalStateException.class, () -> client.fetchComments(QUESTION_ID).block());
        wireMockServer.verify(
            moreThanOrExactly(retryPolicy.maxAttempts() + 1),
            getRequestedFor((urlEqualTo(COMMENTS_URL)))
        );
    }

    @Test
    void shouldGetCorrectResponseAfterFetchingQuestionsAfterErrorResponse() {
        //arrange
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        wireMockServer.stubFor(get(urlEqualTo(QUESTION_URL))
            .inScenario("Retry scenario")
            .whenScenarioStateIs(STARTED)
            .willSetStateTo("Retry succeeded")
            .willReturn(aResponse()
                .withStatus(errorStatus)
                .withHeader("Content-Type", "application/json")
            )
        );
        wireMockServer.stubFor(get(urlEqualTo(QUESTION_URL))
            .inScenario("Retry scenario")
            .whenScenarioStateIs("Retry succeeded")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(QUESTION_RESPONSE_BODY)
            )
        );
        //act + assert
        assertThat(client.fetchQuestion(QUESTION_ID).block()).isEqualTo(QUESTION_EXPECTED_RESPONSE);
        wireMockServer.verify(getRequestedFor((urlEqualTo(QUESTION_URL))));
    }

    @Test
    void shouldGetErrorResponseAfterFetchingQuestionsAfterSpendingAllAttempts() {
        //arrange
        int errorStatus = Integer.parseInt(retryPolicy.statuses().split("-")[0]);
        wireMockServer.stubFor(get(urlEqualTo(QUESTION_URL))
            .willReturn(aResponse()
                .withStatus(errorStatus)
                .withHeader("Content-Type", "application/json"))
        );
        //act + assert
        assertThrows(IllegalStateException.class, () -> client.fetchQuestion(QUESTION_ID).block());
        wireMockServer.verify(
            moreThanOrExactly(retryPolicy.maxAttempts() + 1),
            getRequestedFor((urlEqualTo(QUESTION_URL)))
        );
    }
}
