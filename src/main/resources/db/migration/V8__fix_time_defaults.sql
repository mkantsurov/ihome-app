ALTER TABLE measurements_log_entry ALTER COLUMN created SET DEFAULT timezone('UTC'::text, now());
ALTER TABLE audit_log_entry ALTER COLUMN created SET DEFAULT timezone('UTC'::text, now());
