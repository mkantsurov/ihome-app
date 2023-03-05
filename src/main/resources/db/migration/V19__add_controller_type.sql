ALTER TABLE controller_config_entry ADD COLUMN type INTEGER NOT NULL DEFAULT 0;
ALTER TABLE controller_config_entry ADD COLUMN port INTEGER NOT NULL DEFAULT 80;
INSERT INTO controller_config_entry (id, type, ip_address, port, name) VALUES (5, 2, '192.168.88.75', 8899, 'Garage Controller Usr404');
