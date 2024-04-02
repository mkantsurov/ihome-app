CREATE TABLE error_message_log
(
    id      UUID                        NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    type    INTEGER DEFAULT 0,
    message VARCHAR(255)
);

ALTER TABLE ONLY error_message_log
    ADD CONSTRAINT error_message_log_pkey PRIMARY KEY (id);

CREATE INDEX error_message_log_created_idx ON error_message_log (created);
CREATE INDEX error_message_log_type_idx ON error_message_log (type);


