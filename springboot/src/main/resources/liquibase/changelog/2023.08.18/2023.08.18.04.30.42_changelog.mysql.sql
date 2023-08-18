-- liquibase formatted sql

-- changeset John:1692333059305-1
ALTER TABLE verification_code_email_entity ADD has_used BIT(1) NOT NULL;

-- changeset John:1692333059305-2
ALTER TABLE verification_code_email_entity ADD is_passed BIT(1) NOT NULL;

-- changeset John:1692333059305-3
ALTER TABLE verification_code_email_entity DROP COLUMN is_deleted;

