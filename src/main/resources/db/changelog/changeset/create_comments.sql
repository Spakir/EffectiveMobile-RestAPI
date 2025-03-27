CREATE TABLE IF NOT EXISTS Comments (
    id BIGSERIAL PRIMARY KEY,
    text VARCHAR(200) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    task_id BIGINT NOT NULL,
    user_id BIGINT,

    CONSTRAINT fk_comment_task FOREIGN KEY (task_id)
        REFERENCES public.tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id)
        REFERENCES public.users(id) ON DELETE SET NULL
);