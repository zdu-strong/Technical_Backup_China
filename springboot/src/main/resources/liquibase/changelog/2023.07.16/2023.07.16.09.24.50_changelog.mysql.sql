-- liquibase formatted sql

-- changeset John:1689499504570-1
ALTER TABLE logger_entity MODIFY exception_message VARCHAR(1000);

-- changeset John:1689499504570-2
ALTER TABLE logger_entity MODIFY exception_stack_trace LONGTEXT;

-- changeset John:1689499504570-3
ALTER TABLE logger_entity MODIFY message VARCHAR(1000);

-- changeset John:1689499504570-4
ALTER TABLE long_term_task_entity MODIFY result LONGTEXT;

