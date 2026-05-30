INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (93, 2, 13, 1, 'Bathroom (1-st floor) Mirror Light PS');

INSERT INTO module_config_entry (id, mode, name, group_id, display, type, module_assignment, startup_mode)
VALUES (47, 2, 'Bathroom (1-st floor) Mirror Light control module', 2, true, 22, 4, 0);

INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode)
VALUES (116, 47, 'Bathroom (1-st floor) Mirror Light SW', 2, 92, 1);

INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode)
VALUES (117, 47, 'Bath room (1-st floor)  motion sensor', 2, 34, 1);

INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode)
VALUES (118, 47, 'Bathroom (1-st floor) Mirror Light PS', 1, 93, 1);


