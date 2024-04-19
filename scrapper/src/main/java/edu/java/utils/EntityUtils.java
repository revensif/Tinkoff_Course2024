package edu.java.utils;

import edu.java.dto.Chat;
import edu.java.dto.ChatLink;
import edu.java.dto.Link;
import edu.java.dto.Question;
import edu.java.dto.entity.ChatEntity;
import edu.java.dto.entity.ChatLinkEntity;
import edu.java.dto.entity.LinkEntity;
import edu.java.dto.entity.QuestionEntity;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityUtils {

    public static ChatEntity chatToChatEntity(Chat chat) {
        return ChatEntity.builder()
            .chatId(chat.chatId())
            .build();
    }

    public static LinkEntity createLinkEntity(String url, OffsetDateTime dateTime) {
        return LinkEntity.builder()
            .url(url)
            .updatedAt(dateTime)
            .build();
    }

    public static Link linkEntityToLink(LinkEntity linkEntity) {
        return new Link(
            linkEntity.getLinkId(),
            URI.create(linkEntity.getUrl()),
            linkEntity.getUpdatedAt()
        );
    }

    public static LinkEntity linkToLinkEntity(Link link) {
        return LinkEntity.builder()
            .linkId(link.linkId())
            .url(link.url().toString())
            .updatedAt(link.updatedAt())
            .build();
    }

    public static QuestionEntity createQuestionEntity(long linkId, int answerCount, int commentCount) {
        return QuestionEntity.builder()
            .linkId(linkId)
            .answerCount(answerCount)
            .commentCount(commentCount)
            .build();
    }

    public static Question questionEntityToQuestion(QuestionEntity questionEntity) {
        return new Question(
            questionEntity.getLinkId(),
            questionEntity.getAnswerCount(),
            questionEntity.getCommentCount()
        );
    }

    public static QuestionEntity questionToQuestionEntity(Question question) {
        return QuestionEntity.builder()
            .linkId(question.linkId())
            .answerCount(question.answerCount())
            .commentCount(question.commentCount())
            .build();
    }

    public static ChatLinkEntity createChatLinkEntity(long tgChatId, long linkId) {
        return ChatLinkEntity.builder()
            .chatId(tgChatId)
            .linkId(linkId)
            .build();
    }

    public static ChatLink chatLinkEntityToChatLink(ChatLinkEntity chatLinkEntity) {
        return new ChatLink(
            chatLinkEntity.getChatId(),
            chatLinkEntity.getLinkId()
        );
    }

    public static ChatLinkEntity chatLinkToChatLinkEntity(ChatLink chatLink) {
        return ChatLinkEntity.builder()
            .chatId(chatLink.chatId())
            .linkId(chatLink.linkId())
            .build();
    }
}
