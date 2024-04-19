package edu.java.scrapper.dao.repository.jpa;

import edu.java.dao.repository.jpa.JpaLinkRepository;
import edu.java.dao.repository.jpa.JpaQuestionRepository;
import edu.java.dao.repository.jpa.inner_repository.InnerJpaQuestionRepository;
import edu.java.dto.Question;
import edu.java.scrapper.IntegrationTest;
import edu.java.utils.EntityUtils;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.database-access-type=jpa")
@Transactional
@DirtiesContext
public class JpaQuestionRepositoryTest extends IntegrationTest {

    private static final int ANSWER_COUNT = 5;
    private static final int COMMENT_COUNT = 7;
    private static final URI FIRST_URL = URI.create("link1.com");
    private static final URI SECOND_URL = URI.create("link2.com");

    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private JpaQuestionRepository questionRepository;

    @Autowired
    private InnerJpaQuestionRepository innerQuestionRepository;

    @Test
    public void shouldAddQuestionToDatabase() {
        //arrange
        assertThat(getAllQuestions()).isEmpty();
        long[] linkIds = addLinksToDatabase();
        //act
        Question actual = questionRepository.addQuestion(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        Question expected = new Question(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        //assert
        assertThat(actual).isEqualTo(expected);
        assertThat(getAllQuestions().size()).isEqualTo(1);
        assertThat(getAllQuestions()).isEqualTo(List.of(expected));
    }

    @Test
    public void shouldFindQuestionByLinkId() {
        //arrange
        long[] linkIds = addLinksToDatabase();
        //act
        questionRepository.addQuestion(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        Question expected = new Question(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        //assert
        assertThat(questionRepository.findByLinkId(linkIds[0])).isEqualTo(expected);
        assertThat(questionRepository.findByLinkId(linkIds[1])).isNull();
    }

    @Test
    public void shouldChangeQuestionAnswerCount() {
        //arrange
        long[] linkIds = addLinksToDatabase();
        //act
        questionRepository.addQuestion(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        assertThat(questionRepository.findByLinkId(linkIds[0]).answerCount()).isEqualTo(ANSWER_COUNT);
        //act + assert
        questionRepository.changeAnswerCount(linkIds[0], ANSWER_COUNT + 1);
        assertThat(questionRepository.findByLinkId(linkIds[0]).answerCount()).isEqualTo(ANSWER_COUNT + 1);
    }

    @Test
    public void shouldChangeQuestionCommentCount() {
        //arrange
        long[] linkIds = addLinksToDatabase();
        questionRepository.addQuestion(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        //assert
        assertThat(questionRepository.findByLinkId(linkIds[0]).commentCount()).isEqualTo(COMMENT_COUNT);
        //act + assert
        questionRepository.changeCommentCount(linkIds[0], COMMENT_COUNT + 1);
        assertThat(questionRepository.findByLinkId(linkIds[0]).commentCount()).isEqualTo(COMMENT_COUNT + 1);
    }

    @Test
    public void shouldRemoveQuestion() {
        //arrange
        long[] linkIds = addLinksToDatabase();
        Question firstQuestion = new Question(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        Question secondQuestion = new Question(linkIds[1], ANSWER_COUNT, COMMENT_COUNT);
        questionRepository.addQuestion(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        questionRepository.addQuestion(linkIds[1], ANSWER_COUNT, COMMENT_COUNT);
        assertThat(getAllQuestions().size()).isEqualTo(2);
        //act + assert
        assertThat(questionRepository.removeQuestion(linkIds[0])).isEqualTo(firstQuestion);
        assertThat(getAllQuestions().size()).isEqualTo(1);
        assertThat(questionRepository.removeQuestion(linkIds[1])).isEqualTo(secondQuestion);
        assertThat(getAllQuestions().size()).isEqualTo(0);
    }

    private List<Question> getAllQuestions() {
        return innerQuestionRepository.findAll()
            .stream()
            .map(EntityUtils::questionEntityToQuestion)
            .toList();
    }

    private long[] addLinksToDatabase() {
        linkRepository.add(FIRST_URL);
        linkRepository.add(SECOND_URL);
        return new long[] {linkRepository.findByUri(FIRST_URL).linkId(),
            linkRepository.findByUri(SECOND_URL).linkId()};
    }
}
