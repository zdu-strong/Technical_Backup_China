-- liquibase formatted sql

-- changeset John:1692282981724-1
CREATE TABLE verification_code_email_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, email VARCHAR(512) NOT NULL, is_deleted BIT(1) NOT NULL, update_date datetime NOT NULL, verification_code VARCHAR(255) NOT NULL, CONSTRAINT PK_VERIFICATION_CODE_EMAIL_ENTITY PRIMARY KEY (id));

-- changeset John:1692282981724-2
CREATE INDEX IDXh2tw4b9y7sentv7whtxxxnv3o ON user_email_entity(email, is_deleted);

-- changeset John:1692282981724-3
CREATE INDEX IDXoxeihgs43v794u9lhrh79ugm5 ON verification_code_email_entity(email, create_date);

-- changeset John:1692282981724-4
ALTER TABLE user_entity DROP COLUMN has_registered;

-- changeset John:1692282981724-5
ALTER TABLE user_email_entity DROP COLUMN verification_code;

