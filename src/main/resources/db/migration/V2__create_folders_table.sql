CREATE TABLE folders
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    user_id    BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    parent_id  BIGINT REFERENCES folders (id) ON DELETE CASCADE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_folder_name_per_user_parent
        UNIQUE (user_id, parent_id, name)
);

CREATE INDEX idx_folders_user_id ON folders (user_id);
CREATE INDEX idx_folders_parent_id ON folders (parent_id);