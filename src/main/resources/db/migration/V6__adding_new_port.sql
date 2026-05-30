INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (75, 2, 0, 2, 'Kitchen main Light power switch#2');

INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (76, 2, 7, 1, 'Kitchen main Light Control Relay#2');

INSERT INTO module_config_entry (id, name, module_assignment, group_id, mode, display, type)
VALUES (40, 'Kitchen main Light Control module#2', 4, 2, 2, true, 6);

INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (94, 40, 'Kitchen main Light power switch#2', 2, 75, 1);

INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (95, 40, 'Kitchen main Light Control Relay#2', 1, 76, 1);
