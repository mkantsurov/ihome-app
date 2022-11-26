INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (80, 2, 14, 3, 'Bath room (1-st floor) temperature/humidity sensor');

INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (81, 2, 8, 1, 'Bath Room (1-st flor) Ventilation PS');

INSERT INTO module_config_entry (id, mode, name, group_id, display, type, module_assignment, startup_mode)
VALUES (42, 2, 'Bath Room (1-st floor) Ventilation Control module', 3, true, 10, 6, 0);

-- select * from module_config_element_entry where module_id =26;
--  id | module_id |                        name                        | type | port | display_mode
-- ----+-----------+----------------------------------------------------+------+------+--------------
--  52 |        26 | Bath Room (2-nd floor) Ventilation Control Relay   |    1 |   50 |            1
--  55 |        26 | Bath room (2-nd floor) temperature/humidity sensor |    5 |   52 |            1
--  59 |        26 | Bath room (2-nd floor) motion sensor               |    2 |   47 |            1
--  60 |        26 | Pressure humidity temp. sensor                     |    7 |   36 |            1

INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (100, 42, 'Bath Room (1-st floor) Ventilation Control Relay', 1, 81, 1);

INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (101, 42, 'Bath room (1-st floor) temperature/humidity sensor', 5, 80, 1);

INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (102, 42, 'Bath room (1-st floor) motion sensor',2, 34, 1);

INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (103, 42, 'Pressure humidity temp. sensor', 1, 76, 1);
