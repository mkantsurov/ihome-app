ALTER TABLE measurements_log_entry RENAME COLUMN power_stat TO ext_pwr_voltage;
ALTER TABLE measurements_log_entry ADD COLUMN ext_pwr_current INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry ADD COLUMN ext_pwr_frequency INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry ADD COLUMN ext_pwr_consumption INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry ADD COLUMN int_pwr_current INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry ADD COLUMN int_pwr_current INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry ADD COLUMN int_pwr_frequency INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry ADD COLUMN int_pwr_consumption INTEGER NOT NULL DEFAULT 0;
