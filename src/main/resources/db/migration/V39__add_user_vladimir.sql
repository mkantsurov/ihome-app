-- Add user vladimir with role ADMIN (role ordinal = 1)
-- Default password: vladimir123 (should be changed on first login)

INSERT INTO user_entry (id, username, password)
VALUES (3, 'vladimir', '$2b$12$ApcvlS7ZjiinV5UoeuPh6eVapNtpvfVmwFCiYUeBakrjGOastqmCK');

INSERT INTO user_role_entry (id, user_id, role)
VALUES (3, 3, 1);

SELECT setval('user_entry_id_seq', (SELECT max(id) FROM user_entry))
    INTO TEMP TABLE tmp;
DROP TABLE tmp;

SELECT setval('user_role_entry_id_seq', (SELECT max(id) FROM user_role_entry))
    INTO TEMP TABLE tmp;
DROP TABLE tmp;
