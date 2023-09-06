-- liquibase formatted sql

-- changeset John:1693986578199-1
ALTER TABLE user_entity ADD password LONGTEXT NOT NULL;

