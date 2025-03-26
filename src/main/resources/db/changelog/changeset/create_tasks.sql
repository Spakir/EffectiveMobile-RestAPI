CREATE TABLE IF NOT EXISTS Tasks (
    id BIGSERIAL PRIMARY KEY,
    header VARCHAR(100) NOT NULL,
    description VARCHAR(200) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('OPEN', 'IN_PROGRESS', 'COMPLETED')),
    priority VARCHAR(20) CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    author_id BIGINT NOT NULL,
    executor_id BIGINT,
    CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES public.users(id)  ON DELETE RESTRICT,
    CONSTRAINT fk_executor FOREIGN KEY (executor_id) REFERENCES public.users(id) ON DELETE SET NULL
);