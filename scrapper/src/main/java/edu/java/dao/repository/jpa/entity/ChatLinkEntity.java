package edu.java.dao.repository.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "chat_link")
@IdClass(ChatLinkEntity.ChatLinkEntityId.class)
public class ChatLinkEntity {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Id
    @Column(name = "link_id")
    private Long linkId;

    public static class ChatLinkEntityId implements Serializable {

        private Long chatId;
        private Long linkId;
    }
}
