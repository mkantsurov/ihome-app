ALTER TABLE measurements_log_entry
    ADD COLUMN security_mode INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry
    ADD COLUMN pw_src_converter_mode INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry
    ADD COLUMN pw_src_direct_mode INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry
    ADD COLUMN heating_pump_ff_mode INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry
    ADD COLUMN heating_pump_sf_mode INTEGER NOT NULL DEFAULT 0;
