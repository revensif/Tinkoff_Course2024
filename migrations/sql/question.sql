CREATE TABLE IF NOT EXISTS question
(
    link_id BIGINT REFERENCES link ON DELETE CASCADE,
    answer_count INT,
    comment_count INT,

    PRIMARY KEY (link_id)
)
