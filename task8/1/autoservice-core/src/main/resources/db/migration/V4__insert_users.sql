-- Flyway migration V4: начальные пользователи (логин/пароль для теста)
-- Пароль для обоих: "password" (BCrypt, 10 rounds)

INSERT INTO app_users (username, password_hash, role) VALUES
    ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ADMIN'),
    ('user', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'USER');
