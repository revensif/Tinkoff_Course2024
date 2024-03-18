package edu.java.scrapper.dao.repository.jooq;

import edu.java.dao.repository.jooq.JooqLinkRepository;
import edu.java.dao.repository.jooq.JooqQuestionRepository;
import edu.java.dto.Question;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.util.List;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.dao.jooq.Tables.QUESTION;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JooqQuestionRepositoryTest extends IntegrationTest {

    private static final int ANSWER_COUNT = 5;
    private static final int COMMENT_COUNT = 7;
    private static final URI FIRST_URL = URI.create("link1.com");
    private static final URI SECOND_URL = URI.create("link2.com");

    @Autowired
    private JooqLinkRepository linkRepository;

    @Autowired
    private JooqQuestionRepository questionRepository;

    @Autowired
    private DSLContext dslContext;

    @Test
    @Transactional
    @Rollback
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
    @Transactional
    @Rollback
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

    private List<Question> getAllQuestions() {
        return dslContext.select(QUESTION.fields())
            .from(QUESTION)
            .fetchInto(Question.class);
    }

    private long[] addLinksToDatabase() {
        linkRepository.add(FIRST_URL);
        linkRepository.add(SECOND_URL);
        return new long[] {linkRepository.findByUri(FIRST_URL).getLinkId(),
            linkRepository.findByUri(SECOND_URL).getLinkId()};
    }
}
