package edu.java.scrapper.client.stackoverflow;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.client.stackoverflow.StackOverflowWebClient;
import edu.java.dto.Link;
import edu.java.dto.stackoverflow.CommentsResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.updates.UpdatesInfo;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static edu.java.scrapper.client.stackoverflow.StackOverflowJsonResponse.COMMENTS_RESPONSE_BODY;
import static edu.java.scrapper.client.stackoverflow.StackOverflowJsonResponse.QUESTION_RESPONSE_BODY;
import static org.assertj.core.api.Assertions.assertThat;

public class StackOverflowWebClientTest {

    private static final URI LINK_URL = URI.create("https://stackoverflow.com/questions/12345/test_for_hw2");
    private static final String QUESTION_URL = "/questions/12345?site=stackoverflow";
    private static final String COMMENTS_URL = "/questions/12345/comments?site=stackoverflow";
    private static final Long QUESTION_ID = 12345L;
    private static final Long FIRST_COMMENT_ID = 110156174L;
    private static final Long SECOND_COMMENT_ID = 96880783L;
    private static final Integer ANSWER_COUNT = 17;
    private static final OffsetDateTime DATE_TIME =
        OffsetDateTime.ofInstant(Instant.ofEpochSecond(1708698398L), ZoneOffset.UTC);
    private static final Link LINK = new Link(QUESTION_ID, LINK_URL, DATE_TIME);
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
        //arrange
        StackOverflowClient client = new StackOverflowWebClient(wireMockServer.baseUrl());
        //act
        QuestionResponse response = client.fetchQuestion(QUESTION_ID).block();
        //assert
        assertThat(response).isEqualTo(QUESTION_EXPECTED_RESPONSE);
    }

    @Test
    public void shouldFetchComments() {
        //arrange
        StackOverflowClient client = new StackOverflowWebClient(wireMockServer.baseUrl());
        //act
        CommentsResponse response = client.fetchComments(QUESTION_ID).block();
        //assert
        assertThat(response).isEqualTo(COMMENTS_EXPECTED_RESPONSE);
    }

    @Test
    public void shouldGetUpdatesInfo() {
        //arrange
        StackOverflowClient client = new StackOverflowWebClient(wireMockServer.baseUrl());
        //act
        UpdatesInfo firstUpdatesInfo = client.getUpdatesInfo(LINK, ANSWER_COUNT, 2);
        LINK.setUpdatedAt(DATE_TIME.minusDays(1));
        UpdatesInfo secondUpdatesInfo = client.getUpdatesInfo(LINK, ANSWER_COUNT - 1, 2);
        UpdatesInfo thirdUpdatesInfo = client.getUpdatesInfo(LINK, ANSWER_COUNT, 1);
        UpdatesInfo fourthUpdatesInfo = client.getUpdatesInfo(LINK, ANSWER_COUNT, 2);
        //assert
        assertThat(firstUpdatesInfo.updatedAt()).isEqualTo(DATE_TIME);
        assertThat(firstUpdatesInfo.isSomethingUpdated()).isFalse();
        assertThat(firstUpdatesInfo.message()).isEqualTo("There are no updates!");

        assertThat(secondUpdatesInfo.updatedAt()).isEqualTo(DATE_TIME);
        assertThat(secondUpdatesInfo.isSomethingUpdated()).isTrue();
        assertThat(secondUpdatesInfo.message()).isEqualTo("There is a new answer!");

        assertThat(thirdUpdatesInfo.updatedAt()).isEqualTo(DATE_TIME);
        assertThat(thirdUpdatesInfo.isSomethingUpdated()).isTrue();
        assertThat(thirdUpdatesInfo.message()).isEqualTo("There is a new comment!");

        assertThat(fourthUpdatesInfo.updatedAt()).isEqualTo(DATE_TIME);
        assertThat(fourthUpdatesInfo.isSomethingUpdated()).isTrue();
        assertThat(fourthUpdatesInfo.message()).isEqualTo("The question has been updated!");
    }
}
