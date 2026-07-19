ALTER TABLE module_config_entry
    ADD COLUMN permission JSONB NOT NULL DEFAULT '[]'::jsonb;
