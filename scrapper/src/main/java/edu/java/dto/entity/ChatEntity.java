package edu.java.dto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chat")
public class ChatEntity {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @ManyToMany(mappedBy = "chats", fetch = FetchType.LAZY)
    private List<LinkEntity> links;
}
