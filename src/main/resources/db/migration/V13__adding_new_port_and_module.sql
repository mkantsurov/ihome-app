INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (79, 1, 15, 1, 'Garage inverter cooling PS');

INSERT INTO module_config_entry (id, name, group_id, mode, display, type, module_assignment)
VALUES (41, 'Garage inverter cooling control module', 1, 2, true, 17, 6);

INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (97, 41, 'Garage inverter cooling power control relay', 1, 79, 1);

INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (98, 41, 'Garage temperature sensor', 5, 6, 1);
