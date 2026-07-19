-- Add user miroslava with role CHILDREN_ROOM2_MANAGER (role ordinal = 4)
-- Default password: miroslava123 (should be changed on first login)

INSERT INTO user_entry (id, username, password)
VALUES (4, 'miroslava', '$2b$12$SebBav8OqO48HtCKq2RVyuFqrLHRTe5XJf5u2FL8v6birHEIy1sZ6');

INSERT INTO user_role_entry (id, user_id, role)
VALUES (4, 4, 4);

SELECT setval('user_entry_id_seq', (SELECT max(id) FROM user_entry))
    INTO TEMP TABLE tmp;
DROP TABLE tmp;

SELECT setval('user_role_entry_id_seq', (SELECT max(id) FROM user_role_entry))
    INTO TEMP TABLE tmp;
DROP TABLE tmp;
