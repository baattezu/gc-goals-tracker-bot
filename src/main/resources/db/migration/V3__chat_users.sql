CREATE TABLE chat (
                      id BIGSERIAL PRIMARY KEY,
                      chat_name VARCHAR(255)
);
-- Изменение таблицы пользователей, добавление связи с чатами
ALTER TABLE users
ADD COLUMN is_pinned BOOLEAN DEFAULT FALSE;

-- Удаление ограничения NOT NULL для колонки chat_id
ALTER TABLE users ALTER COLUMN chat_id DROP NOT NULL;
-- Обновление всех значений chat_id на NULL
UPDATE users SET chat_id = NULL;

-- Добавление внешнего ключа
ALTER TABLE users
    ADD CONSTRAINT fk_chat_user
        FOREIGN KEY (chat_id) REFERENCES chat(id);
