DELETE FROM controller_port_config_entry WHERE id=45;
DELETE FROM controller_port_config_entry WHERE id=46;

ALTER TABLE controller_port_config_entry ADD CONSTRAINT controller_port_uniq UNIQUE (port_address, controller_id);
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (73, 1, 7, 2, 'Security Mode');

ALTER TABLE module_config_entry ADD COLUMN module_assignment INTEGER NOT NULL DEFAULT 0;

INSERT INTO module_config_entry (id, name, module_assignment, group_id, mode, display, type)
VALUES (39, 'Sliding Gates power control module', 3, 2, 2, true, 15);
UPDATE module_config_entry SET type = 15, module_assignment = 3 WHERE id = 1;

-- POWER_SUPPLY,           //1
--     HEATING_CONTROL,        //2
--     GATE_CONTROL,           //3
--     LIGHT_CONTROL,          //4
--     EXT_LIGHT_CONTROL       //5
--     VENTILATION_CONTROL     //6

UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Garage light control module';
UPDATE module_config_entry SET module_assignment = 6 WHERE name = 'Garage ventilation control module';
UPDATE module_config_entry SET module_assignment = 5 WHERE name = 'Exterior light  control module';
UPDATE module_config_entry SET module_assignment = 2 WHERE name = 'Garage heating system pump control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Garden light power control module';
UPDATE module_config_entry SET module_assignment = 1 WHERE name = 'Second Flor 220v PS control module';
UPDATE module_config_entry SET module_assignment = 6 WHERE name = 'Recuperator PS control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Wardrobe light PS control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Children room #1 Light PS control module #1';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Children room #1 Light PS control module #2';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Bedroom Light PS control module #1';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Bedroom Light PS control module #2';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Children room #2 Light PS control module #1';
UPDATE module_config_entry SET module_assignment = 1 WHERE name = 'First Flor 220v PS standby control module';
UPDATE module_config_entry SET module_assignment = 1 WHERE name = 'First Flor 220v PS generic control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Bath Room (1-st floor) Light Control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Corridor Light Control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Kitchen main Light Control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Living room Light Control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Cabinet Light Control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Corridor 2-nd flor light control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Bath Room (2-nd floor) Light Control module';
UPDATE module_config_entry SET module_assignment = 6 WHERE name = 'Bath Room (2-nd floor) Ventilation Control module';
UPDATE module_config_entry SET module_assignment = 2 WHERE name = 'Heat Water recirculation pump control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Stairwell light control module';
UPDATE module_config_entry SET module_assignment = 1 WHERE name = 'Input Source Direct Power Control Module';
UPDATE module_config_entry SET module_assignment = 1 WHERE name = 'Input Source Converter Power Control Module';
UPDATE module_config_entry SET module_assignment = 2 WHERE name = 'Garage heating system pump control module#2';
UPDATE module_config_entry SET module_assignment = 2 WHERE name = 'Garage Solar System PS module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Loft light PS control module';
UPDATE module_config_entry SET module_assignment = 1 WHERE name = 'Automatic water tap control module';
UPDATE module_config_entry SET module_assignment = 2 WHERE name = 'Floor heating pump control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Box room Light control module';
UPDATE module_config_entry SET module_assignment = 1 WHERE name = 'Water Pump PS control module';
UPDATE module_config_entry SET module_assignment = 4 WHERE name = 'Kitchen Illumination of the working area Control module';

INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (91, 1, 'Security Mode Sensor', 2, 73, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (92, 39, 'Security Mode Sensor', 2, 73, 1);
