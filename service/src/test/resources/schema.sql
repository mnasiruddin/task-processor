
CREATE TABLE IF NOT EXISTS task (
    id SERIAL,
    task_id VARCHAR(255) NOT NULL PRIMARY KEY,
    total_duration_ms BIGINT NOT NULL,
    counter BIGINT NOT NULL
);
