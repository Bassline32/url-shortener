CREATE TABLE url_tags
(
    url_id BIGINT NOT NULL REFERENCES short_url (id) ON DELETE CASCADE,
    tad_id BIGINT NOT NULL REFERENCES tags (id) ON DELETE CASCADE,
    PRIMARY KEY (url_id, tad_id)
);
CREATE INDEX idx_url_tag_id ON url_tags(tad_id);