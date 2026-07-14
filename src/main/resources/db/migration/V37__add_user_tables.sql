CREATE SEQUENCE user_entry_id_seq INCREMENT 1;

CREATE TABLE user_entry
(
    id       BIGINT DEFAULT nextval('user_entry_id_seq') NOT NULL,
    username VARCHAR(64)  NOT NULL,
    password VARCHAR(255) NOT NULL
);

ALTER TABLE user_entry
    ADD CONSTRAINT user_entry_pkey PRIMARY KEY (id);

CREATE UNIQUE INDEX user_entry_username_idx
    ON user_entry (username);

CREATE SEQUENCE user_role_entry_id_seq INCREMENT 1;

CREATE TABLE user_role_entry
(
    id      BIGINT DEFAULT nextval('user_role_entry_id_seq') NOT NULL,
    user_id BIGINT NOT NULL,
    role    INTEGER NOT NULL
);

ALTER TABLE user_role_entry
    ADD CONSTRAINT user_role_entry_pkey PRIMARY KEY (id);

ALTER TABLE user_role_entry
    ADD CONSTRAINT user_role_entry_user_fk FOREIGN KEY (user_id) REFERENCES user_entry (id);

CREATE INDEX user_role_entry_user_id_idx
    ON user_role_entry (user_id);

INSERT INTO user_entry (id, username, password)
VALUES (1, 'admin', '$2a$10$9L3UYusKc9VYjzHSgAjo.eXbKqnj72O0qIgo/ty6jEWw2GFuQkPSK');

INSERT INTO user_role_entry (id, user_id, role)
VALUES (1, 1, 1);

SELECT setval('user_entry_id_seq', (SELECT max(id) FROM user_entry))
    INTO TEMP TABLE tmp;
DROP TABLE tmp;

SELECT setval('user_role_entry_id_seq', (SELECT max(id) FROM user_role_entry))
    INTO TEMP TABLE tmp;
DROP TABLE tmp;
