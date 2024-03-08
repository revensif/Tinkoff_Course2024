CREATE TABLE IF NOT EXISTS link
(
    link_id    BIGINT GENERATED ALWAYS AS IDENTITY,
    uri        TEXT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,

    PRIMARY KEY (link_id),
    UNIQUE (uri)
)
