INSERT INTO controller_port_config_entry(id, controller_id, port_address, type, description) VALUES (88, 5, 3, 9, 'Garage Power Meter #3');
ALTER TABLE measurements_log_entry ADD COLUMN int_bck_pwr_voltage INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry ADD COLUMN int_bck_pwr_current INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry ADD COLUMN int_bck_pwr_frequency INTEGER NOT NULL DEFAULT 0;
ALTER TABLE measurements_log_entry ADD COLUMN int_bck_pwr_consumption INTEGER NOT NULL DEFAULT 0;
