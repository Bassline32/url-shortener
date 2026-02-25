CREATE TABLE clicks
(
    id           BIGSERIAL PRIMARY KEY,
    short_url_id BIGINT    NOT NULL REFERENCES short_urls (id) ON DELETE CASCADE,
    clicked_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address   VARCHAR(45),
    user_agent   VARCHAR(500),
    referer      VARCHAR(2048)
);
CREATE INDEX idx_clicks_short_url_id ON clicks (short_url_id);
CREATE INDEX idx_clicks_clicked_at ON clicks (clicked_at);