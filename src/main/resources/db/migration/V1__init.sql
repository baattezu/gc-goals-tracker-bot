-- Create table for User
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    private_chat_id BIGINT,
    group_chat_id BIGINT,
    results_for_week VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS groupchat (
    id BIGSERIAL PRIMARY KEY,
    chat_name VARCHAR(255)
);
ALTER TABLE users ADD CONSTRAINT fk_chat_user
    FOREIGN KEY (group_chat_id) REFERENCES groupchat(id);
-- Create table for Goal
CREATE TABLE IF NOT EXISTS goals (
    id BIGSERIAL PRIMARY KEY,
    goal text NOT NULL,
    reward text,
    created_at TIMESTAMP NOT NULL,
    deadline TIMESTAMP NOT NULL,
    completed BOOLEAN NOT NULL,
    user_id BIGINT, CONSTRAINT fk_goal_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
-- Create table for CheckIn
CREATE TABLE IF NOT EXISTS check_in (
    id BIGSERIAL PRIMARY KEY,
    check_in_time TIMESTAMP NOT NULL,
    user_id BIGINT, CONSTRAINT fk_checkin_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
