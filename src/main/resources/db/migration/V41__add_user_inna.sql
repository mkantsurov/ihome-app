-- Add user inna with role AUTHORIZED_GUEST (role ordinal = 5)
-- Default password: inna123 (should be changed on first login)

INSERT INTO user_entry (id, username, password)
VALUES (5, 'inna', '$2b$12$IvFNL3y3gQQZK0r0FXccRu7zTxScQY/u.moo3zLv/oGD8FgpHu3J6');

INSERT INTO user_role_entry (id, user_id, role)
VALUES (5, 5, 5);

SELECT setval('user_entry_id_seq', (SELECT max(id) FROM user_entry))
    INTO TEMP TABLE tmp;
DROP TABLE tmp;

SELECT setval('user_role_entry_id_seq', (SELECT max(id) FROM user_role_entry))
    INTO TEMP TABLE tmp;
DROP TABLE tmp;
