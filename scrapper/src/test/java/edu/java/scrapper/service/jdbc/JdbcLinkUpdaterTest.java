package edu.java.scrapper.service.jdbc;

import edu.java.client.bot.HttpBotClient;
import edu.java.client.github.GithubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.dao.repository.jdbc.JdbcChatLinkRepository;
import edu.java.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.dao.repository.jdbc.JdbcQuestionRepository;
import edu.java.dto.Link;
import edu.java.dto.Question;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.jdbc.JdbcLinkUpdater;
import edu.java.updates.UpdatesInfo;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JdbcLinkUpdaterTest extends IntegrationTest {

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final int ANSWER_COUNT = 5;
    private static final int COMMENT_COUNT = 3;
    private static final URI GITHUB_URL = URI.create("https://github.com/revensif/Tinkoff_Course2024");
    private static final URI STACKOVERFLOW_URL = URI.create("https://stackoverflow.com/questions/12345/test_for_hw5");
    private static final OffsetDateTime OLD_DATE = OffsetDateTime.now().minusDays(11L).truncatedTo(ChronoUnit.MILLIS);

    @Mock
    private JdbcQuestionRepository questionRepository;

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
    @Transactional
    @Rollback
    public void shouldUpdateAndGetTwoOutdatedLinks() {
        //arrange
        List<Link> outdatedLinks = Arrays.asList(
            new Link(FIRST_ID, GITHUB_URL, OLD_DATE),
            new Link(SECOND_ID, STACKOVERFLOW_URL, OLD_DATE)
        );
        UpdatesInfo updatesInfo = new UpdatesInfo(true, OffsetDateTime.now(), "updated");
        Question question = new Question(FIRST_ID, ANSWER_COUNT, COMMENT_COUNT);

        when(linkRepository.findOutdatedLinks(any(Duration.class))).thenReturn(outdatedLinks);
        when(githubClient.getUpdatesInfo(any(Link.class))).thenReturn(updatesInfo);
        when(questionRepository.findByLinkId(any(Long.class))).thenReturn(question);
        when(stackOverflowClient.getUpdatesInfo(
            any(Link.class),
            any(Integer.class),
            any(Integer.class)
        )).thenReturn(updatesInfo);
        //act
        int actual = linkUpdater.update();
        //assert
        assertThat(actual).isEqualTo(2);
    }
}
