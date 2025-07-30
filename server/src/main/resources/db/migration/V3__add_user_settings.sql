-- Добавление полей настроек пользователя
ALTER TABLE users ADD COLUMN email_notifications BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN sms_notifications BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD COLUMN theme VARCHAR(10) NOT NULL DEFAULT 'auto'; 