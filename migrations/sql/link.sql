CREATE TABLE IF NOT EXISTS link
(
    link_id    BIGSERIAL,
    uri        TEXT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,

    PRIMARY KEY (link_id),
    UNIQUE (uri)
)
