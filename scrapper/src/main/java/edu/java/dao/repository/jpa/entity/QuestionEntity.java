package edu.java.dao.repository.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "question")
public class QuestionEntity {

    @Id
    @Column(name = "link_id")
    private Long linkId;

    @Column(name = "answer_count")
    private Integer answerCount;

    @Column(name = "comment_count")
    private Integer commentCount;

    @OneToOne
    @MapsId
    private LinkEntity link;
}
