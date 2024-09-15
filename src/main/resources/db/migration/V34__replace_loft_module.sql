UPDATE controller_port_config_entry
SET description='Bedroom light PS (wall sconces)'
WHERE id = 61;

UPDATE module_config_entry
SET name='Bedroom light PS (wall sconces)',
    type=21
WHERE id = 33;

UPDATE module_config_element_entry SET name='Bedroom light (wall sconces) PS' WHERE id = 77;

INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (90, 3, 20, 2, 'Bedroom light (wall sconces) SW Left');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (91, 3, 21, 2, 'Bedroom light (wall sconces) SW Right');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (92, 2, 5, 2, 'Bathroom (1-st floor) Mirror Light SW');

INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode)
VALUES (111, 33, 'Bedroom light (wall sconces) SW Left', 2, 90, 1);

INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode)
VALUES (112, 33, 'Bedroom light (wall sconces) SW Right', 2, 91, 1);

INSERT INTO module_config_entry (id, mode, name, group_id, display, type, module_assignment, startup_mode)
VALUES (46, 2, 'Bathroom Mirror (2-nd floor) Light control module', 3, true, 22, 4, 0);

INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode)
VALUES (113, 46, 'Bathroom (2-nd floor) Mirror Light SW', 2, 84, 1);

INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode)
VALUES (114, 46, 'Bath room (2-nd floor) motion sensor', 2, 47, 1);

INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode)
VALUES (115, 46, 'Bathroom (2-nd floor) Mirror Light PS', 1, 24, 1);

-- INSERT INTO module_config_entry (id, mode, name, group_id, display, type, module_assignment, startup_mode)
-- VALUES (47, 2, 'Bathroom (1-st floor) Mirror Light control module', 2, true, 22, 4, 0);
--
-- INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode)
-- VALUES (116, 47, 'Bathroom (1-st floor) Mirror Light SW', 2, 92, 1);
--
-- INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode)
-- VALUES (117, 47, 'Bath room (1-st floor)  motion sensor', 2, 34, 1);




