package edu.java.scrapper.service.jooq;

import edu.java.client.bot.HttpBotClient;
import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jooq.JooqChatLinkRepository;
import edu.java.dao.repository.jooq.JooqLinkRepository;
import edu.java.dao.repository.jooq.JooqQuestionRepository;
import edu.java.dto.Link;
import edu.java.dto.Question;
import edu.java.dto.github.RepositoryResponse;
import edu.java.dto.stackoverflow.CommentsResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.jooq.JooqLinkUpdater;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JooqLinkUpdaterTest extends IntegrationTest {

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final long QUESTION_ID = 12345;
    private static final String OWNER = "revensif";
    private static final String REPO = "Tinkoff_Course2024";
    private static final int ANSWER_COUNT = 5;
    private static final URI GITHUB_URL = URI.create("https://github.com/revensif/Tinkoff_Course2024");
    private static final URI STACKOVERFLOW_URL = URI.create("https://stackoverflow.com/questions/12345/test_for_hw5");
    private static final OffsetDateTime OLD_DATE = OffsetDateTime.now().minusDays(11L).truncatedTo(ChronoUnit.MILLIS);

    @Mock
    private JooqQuestionRepository questionRepository;

    @Mock
    private JooqLinkRepository linkRepository;

    @Mock
    private JooqChatLinkRepository chatLinkRepository;

    @Mock
    private GithubClient githubClient;

    @Mock
    private StackOverflowClient stackOverflowClient;

    @Mock
    private HttpBotClient httpBotClient;

    @Mock
    private List<String> resources;

    @InjectMocks
    private JooqLinkUpdater linkUpdater;

    @Test
    @Transactional
    @Rollback
    public void shouldUpdateAndGetTwoOutdatedLinks() {
        //arrange
        List<Link> outdatedLinks = Arrays.asList(
            new Link(FIRST_ID, GITHUB_URL, OLD_DATE),
            new Link(SECOND_ID, STACKOVERFLOW_URL, OLD_DATE)
        );
        Mono<RepositoryResponse> repositoryResponse = Mono.just(
            new RepositoryResponse(FIRST_ID, GITHUB_URL, OffsetDateTime.now())
        );
        Mono<QuestionResponse> questionResponse = Mono.just(new QuestionResponse(
            List.of(new QuestionResponse.ItemResponse(
                QUESTION_ID,
                STACKOVERFLOW_URL,
                ANSWER_COUNT,
                OffsetDateTime.now()
            )))
        );
        Mono<CommentsResponse> commentsResponse = Mono.just(new CommentsResponse(
            List.of(new CommentsResponse.ItemResponse(FIRST_ID))
        ));
        Question question = new Question(FIRST_ID, ANSWER_COUNT - 2, 1);

        when(linkRepository.findOutdatedLinks(any(Duration.class))).thenReturn(outdatedLinks);
        when(questionRepository.findByLinkId(any(Long.class))).thenReturn(question);
        when(githubClient.fetchRepository(OWNER, REPO)).thenReturn(repositoryResponse);
        when(stackOverflowClient.fetchQuestion(QUESTION_ID)).thenReturn(questionResponse);
        when(stackOverflowClient.fetchComments(QUESTION_ID)).thenReturn(commentsResponse);
        when(resources.getFirst()).thenReturn("github.com");
        when(resources.getLast()).thenReturn("stackoverflow.com");
        //act
        int actual = linkUpdater.update();
        //assert
        assertThat(actual).isEqualTo(2);
    }
}
