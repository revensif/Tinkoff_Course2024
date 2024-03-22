package edu.java.dto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "question")
public class QuestionEntity {

    @Id
    @Column(name = "link_id")
    private Long linkId;

    @Column(name = "answer_count")
    private Integer answerCount;

    @Column(name = "comment_count")
    private Integer commentCount;
}
