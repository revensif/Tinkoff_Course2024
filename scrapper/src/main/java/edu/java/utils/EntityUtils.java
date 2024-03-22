package edu.java.utils;

import edu.java.dto.Chat;
import edu.java.dto.Link;
import edu.java.dto.entity.ChatEntity;
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
}
