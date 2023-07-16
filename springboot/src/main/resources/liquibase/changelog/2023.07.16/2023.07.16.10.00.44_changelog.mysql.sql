-- liquibase formatted sql

-- changeset John:1689501659191-1
ALTER TABLE user_message_entity MODIFY content VARCHAR(4096);

-- changeset John:1689501659191-2
ALTER TABLE user_email_entity MODIFY email VARCHAR(4096);

-- changeset John:1689501659191-3
ALTER TABLE logger_entity MODIFY exception_message VARCHAR(4096);

-- changeset John:1689501659191-4
ALTER TABLE logger_entity MODIFY exception_message VARCHAR(4096) NOT NULL;

-- changeset John:1689501659191-5
ALTER TABLE logger_entity MODIFY exception_stack_trace LONGTEXT NOT NULL;

-- changeset John:1689501659191-6
ALTER TABLE user_message_entity MODIFY file_name VARCHAR(4096);

-- changeset John:1689501659191-7
ALTER TABLE logger_entity MODIFY message VARCHAR(4096);

-- changeset John:1689501659191-8
ALTER TABLE logger_entity MODIFY message VARCHAR(4096) NOT NULL;

-- changeset John:1689501659191-9
ALTER TABLE organize_shadow_entity MODIFY name VARCHAR(4096);

-- changeset John:1689501659191-10
ALTER TABLE encrypt_decrypt_entity MODIFY private_key_ofrsa VARCHAR(4096);

-- changeset John:1689501659191-11
ALTER TABLE token_entity MODIFY private_key_ofrsa VARCHAR(4096);

-- changeset John:1689501659191-12
ALTER TABLE user_entity MODIFY private_key_ofrsa VARCHAR(4096);

-- changeset John:1689501659191-13
ALTER TABLE encrypt_decrypt_entity MODIFY public_key_ofrsa VARCHAR(4096);

-- changeset John:1689501659191-14
ALTER TABLE user_entity MODIFY public_key_ofrsa VARCHAR(4096);

-- changeset John:1689501659191-15
ALTER TABLE encrypt_decrypt_entity MODIFY secret_key_ofaes VARCHAR(4096);

-- changeset John:1689501659191-16
ALTER TABLE friendship_entity MODIFY secret_key_ofaes VARCHAR(4096);

-- changeset John:1689501659191-17
ALTER TABLE user_entity MODIFY username VARCHAR(4096);

