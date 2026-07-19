-- Add user liliana with role SUPERVISOR (role ordinal = 2)
-- Default password: liliana123 (should be changed on first login)

INSERT INTO user_entry (id, username, password)
VALUES (2, 'liliana', '$2b$10$6Fzp9YfuQx3SBcPgT/Tj5e01.h1oyEbDitOi5r0QDhCVEFx4wCfmm');

INSERT INTO user_role_entry (id, user_id, role)
VALUES (2, 2, 2);

SELECT setval('user_entry_id_seq', (SELECT max(id) FROM user_entry))
    INTO TEMP TABLE tmp;
DROP TABLE tmp;

SELECT setval('user_role_entry_id_seq', (SELECT max(id) FROM user_role_entry))
    INTO TEMP TABLE tmp;
DROP TABLE tmp;
