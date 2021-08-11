
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (77, 1, 36, 8, 'Luminosity sensor #2');

UPDATE module_config_element_entry SET port = 77, type=9  WHERE port = 58;

