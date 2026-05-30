DELETE FROM module_config_element_entry WHERE module_id = 12;
DELETE FROM module_config_entry where id=12;

UPDATE controller_port_config_entry SET description = 'Bath Room 2-nd Floor Mirror Light PS' WHERE id=24;
UPDATE controller_port_config_entry SET type = 5 WHERE id=25;
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description) VALUES (83, 3, 12, 1, 'Children room #2 PS #2');

INSERT INTO controller_port_config_entry(id, controller_id, port_address, type, description) VALUES (84, 3, 19, 2, 'Bath Room 2-nd Floor Mirror Light SW');

UPDATE module_config_entry SET type=7 WHERE id=14;
UPDATE module_config_element_entry SET type=6 WHERE id=26;

INSERT INTO module_config_entry (id,mode,name,group_id,display,type,module_assignment,startup_mode) VALUES (44, 1, 'Children room #2 Light PS control module #2', 3, true, 6, 4, 0);
INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode) VALUES (106, 44, 'Children room #2 Light Control Relay #2', 1, 83, 1);
INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode) VALUES (107, 44, 'Children room #2 Light Switch #2', 2, 22, 0);

INSERT INTO module_config_entry (id,mode,name,group_id,display,type,module_assignment,startup_mode) VALUES (45, 1, 'Bath Room Second Floor Mirror Light PS control module', 3, true, 6, 4, 0);
INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode) VALUES (108, 45, 'Bath Room 2-nd Floor Mirror Light PS Relay', 1, 24, 1);
INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode) VALUES (109, 45, 'Bath Room 2-nd Floor Mirror Light SW', 2, 84, 0);

