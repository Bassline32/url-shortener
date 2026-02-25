CREATE TABLE url_tags
(
    url_id BIGINT NOT NULL REFERENCES short_urls (id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tags (id) ON DELETE CASCADE,
    PRIMARY KEY (url_id, tag_id)
);
CREATE INDEX idx_url_tag_id ON url_tags(tag_id);