CREATE TABLE short_urls
(
    id           BIGSERIAL PRIMARY KEY,
    short_code   VARCHAR(20)   NOT NULL UNIQUE,
    original_url VARCHAR(2048) NOT NULL,
    user_id      BIGINT        REFERENCES users (id) ON DELETE SET NULL,
    folder_id    BIGINT        REFERENCES folders (id) ON DELETE SET NULL,
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at   TIMESTAMP,
    click_count  BIGINT                 DEFAULT 0
);
CREATE INDEX idx_short_urls_short_code ON short_urls (short_code);
CREATE INDEX idx_short_urls_user_id ON short_urls (user_id);
CREATE INDEX idx_short_urls_folder_id ON short_urls (folder_id);
CREATE INDEX idx_short_urls_created_at ON short_urls (created_at);
