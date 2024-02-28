package edu.java.scrapper.client.stackoverflow;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.client.stackoverflow.StackOverflowWebClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;

public class StackOverflowWebClientTest {

    private static final String URL = "/questions/12345?site=stackoverflow";
    private static final Long QUESTION_ID = 12345L;
    private static WireMockServer wireMockServer;
    private static final String RESPONSE_BODY = """
        {
            "items": [
                {
                    "tags": [
                        "c",
                        "data-structures",
                        "linked-list"
                    ],
                    "last_activity_date": 1708698398,
                    "question_id": 12345,
                    "link": "https://stackoverflow.com/questions/12345/test_for_hw2"
                }
            ],
            "has_more": true,
            "quota_max": 10000,
            "quota_remaining": 9934
        }
        """;
    private static final QuestionResponse EXPECTED_RESPONSE = new QuestionResponse(
        List.of(
            new QuestionResponse.ItemResponse(
                QUESTION_ID,
                "https://stackoverflow.com/questions/12345/test_for_hw2",
                OffsetDateTime.ofInstant(
                    Instant.ofEpochSecond(1708698398L),
                    ZoneOffset.UTC
                )
            )
        )
    );

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
    public void shouldFetchRepository() {
        //arrange
        wireMockServer.stubFor(get(URL)
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(RESPONSE_BODY)));
        StackOverflowClient client = new StackOverflowWebClient(wireMockServer.baseUrl());
        //act
        QuestionResponse response = client.fetchQuestion(QUESTION_ID).block();
        //assert
        assertThat(response).isEqualTo(EXPECTED_RESPONSE);
    }
}
