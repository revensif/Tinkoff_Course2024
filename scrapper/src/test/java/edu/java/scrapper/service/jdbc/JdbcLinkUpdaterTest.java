package edu.java.scrapper.service.jdbc;

import edu.java.client.bot.HttpBotClient;
import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dto.Link;
import edu.java.dto.github.RepositoryResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.jdbc.JdbcLinkUpdater;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JdbcLinkUpdaterTest extends IntegrationTest {

    private static final long FIRST_ID = 1L;
    private static final long QUESTION_ID = 12345L;
    private static final String OWNER = "revensif";
    private static final String REPO = "Tinkoff_Course2024";
    private static final URI GITHUB_URL = URI.create("https://github.com/revensif/Tinkoff_Course2024");
    private static final URI STACKOVERFLOW_URL = URI.create("https://stackoverflow.com/questions/12345/test_for_hw5");
    private static final OffsetDateTime OLD_DATE = OffsetDateTime.now().minusDays(11L).truncatedTo(ChronoUnit.MILLIS);

    @Mock
    private JdbcLinkRepository linkRepository;

    @Mock
    private JdbcChatLinkRepository chatLinkRepository;

    @Mock
    private GithubClient githubClient;

    @Mock
    private StackOverflowClient stackOverflowClient;

    @Mock
    private HttpBotClient httpBotClient;

    @InjectMocks
    private JdbcLinkUpdater linkUpdater;

    @Test
    public void shouldUpdateAndGetTwoOutdatedLinks() {
        //arrange
        List<Link> outdatedLinks = Arrays.asList(
            new Link(FIRST_ID, GITHUB_URL, OLD_DATE),
            new Link(QUESTION_ID, STACKOVERFLOW_URL, OLD_DATE)
        );
        Mono<RepositoryResponse> repositoryResponse = Mono.just(
            new RepositoryResponse(FIRST_ID, GITHUB_URL, OffsetDateTime.now())
        );
        Mono<QuestionResponse> questionResponse = Mono.just(new QuestionResponse(
            List.of(new QuestionResponse.ItemResponse(QUESTION_ID, STACKOVERFLOW_URL, OffsetDateTime.now())))
        );
        when(linkRepository.findOutdatedLinks(any(Duration.class))).thenReturn(outdatedLinks);
        when(githubClient.fetchRepository(OWNER, REPO)).thenReturn(repositoryResponse);
        when(stackOverflowClient.fetchQuestion(QUESTION_ID)).thenReturn(questionResponse);
        //act
        int actual = linkUpdater.update();
        assertThat(actual).isEqualTo(2);
    }
}
