UPDATE module_config_entry SET type=18 WHERE id=8;
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (99, 8, 'Outdoor temperature/humidity', 5, 11, 1);
