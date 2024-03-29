package edu.java.scrapper.dao.repository.jpa;

import edu.java.dao.repository.jpa.JpaLinkRepository;
import edu.java.dao.repository.jpa.JpaQuestionRepository;
import edu.java.dto.entity.LinkEntity;
import edu.java.dto.entity.QuestionEntity;
import edu.java.scrapper.IntegrationTest;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.utils.EntityUtils.createLinkEntity;
import static edu.java.utils.EntityUtils.createQuestionEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.database-access-type=jpa")
@Transactional
@DirtiesContext
public class JpaQuestionRepositoryTest extends IntegrationTest {

    private static final long FIRST_ID = 1;
    private static final long SECOND_ID = 2;
    private static final int ANSWER_COUNT = 5;
    private static final int COMMENT_COUNT = 7;
    private static final String FIRST_URL = "link1.com";
    private static final String SECOND_URL = "link2.com";
    private static final OffsetDateTime CURRENT_TIME = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    private static final LinkEntity FIRST_LINK_ENTITY = createLinkEntity(FIRST_URL, CURRENT_TIME);
    private static final LinkEntity SECOND_LINK_ENTITY = createLinkEntity(SECOND_URL, CURRENT_TIME);

    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private JpaQuestionRepository questionRepository;

    @Test
    public void shouldAddQuestionToDatabase() {
        //arrange
        assertThat(questionRepository.findAll()).isEmpty();
        long[] linkIds = addLinksToDatabase();
        //act
        QuestionEntity expected = createQuestionEntity(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        QuestionEntity actual = questionRepository.saveAndFlush(expected);
        //assert
        assertThat(actual).isEqualTo(expected);
        assertThat(questionRepository.findAll().size()).isEqualTo(1);
        assertThat(questionRepository.findAll()).isEqualTo(List.of(expected));
    }

    @Test
    public void shouldFindQuestionByLinkId() {
        //arrange
        long[] linkIds = addLinksToDatabase();
        //act
        QuestionEntity question = createQuestionEntity(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        questionRepository.saveAndFlush(question);
        //assert
        assertThat(questionRepository.findById(linkIds[0]).get()).isEqualTo(question);
        assertThat(questionRepository.findById(linkIds[1])).isEmpty();
    }

    @Test
    public void shouldChangeQuestionAnswerCount() {
        //arrange
        long[] linkIds = addLinksToDatabase();
        //act
        QuestionEntity question = createQuestionEntity(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        questionRepository.saveAndFlush(question);
        assertThat(questionRepository.findById(linkIds[0]).get().getAnswerCount()).isEqualTo(ANSWER_COUNT);
        //act + assert
        questionRepository.changeAnswerCount(linkIds[0], ANSWER_COUNT + 1);
        assertThat(questionRepository.findById(linkIds[0]).get().getAnswerCount()).isEqualTo(ANSWER_COUNT + 1);
    }

    @Test
    public void shouldChangeQuestionCommentCount() {
        //arrange
        long[] linkIds = addLinksToDatabase();
        //act
        QuestionEntity question = createQuestionEntity(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        questionRepository.saveAndFlush(question);
        assertThat(questionRepository.findById(linkIds[0]).get().getCommentCount()).isEqualTo(COMMENT_COUNT);
        //act + assert
        questionRepository.changeCommentCount(linkIds[0], COMMENT_COUNT + 1);
        assertThat(questionRepository.findById(linkIds[0]).get().getCommentCount()).isEqualTo(COMMENT_COUNT + 1);
    }

    @Test
    public void shouldRemoveQuestion() {
        //arrange
        long[] linkIds = addLinksToDatabase();
        QuestionEntity firstQuestion = createQuestionEntity(linkIds[0], ANSWER_COUNT, COMMENT_COUNT);
        QuestionEntity secondQuestion = createQuestionEntity(linkIds[1], COMMENT_COUNT, ANSWER_COUNT);
        questionRepository.saveAndFlush(firstQuestion);
        questionRepository.saveAndFlush(secondQuestion);
        assertThat(questionRepository.findAll().size()).isEqualTo(2);
        //act + assert
        questionRepository.delete(firstQuestion);
        assertThat(questionRepository.findAll().size()).isEqualTo(1);
        questionRepository.delete(secondQuestion);
        assertThat(questionRepository.findAll().size()).isEqualTo(0);
    }

    private long[] addLinksToDatabase() {
        linkRepository.saveAndFlush(FIRST_LINK_ENTITY);
        linkRepository.saveAndFlush(SECOND_LINK_ENTITY);
        return new long[] {linkRepository.findByUrl(FIRST_URL).get().getLinkId(),
            linkRepository.findByUrl(SECOND_URL).get().getLinkId()};
    }
}
