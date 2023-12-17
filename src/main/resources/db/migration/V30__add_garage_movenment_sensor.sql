INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (86, 1, 9, 2, 'Garage motion sensor');

-- display_mode  id            module_id     name          port          type
-- ihome=> select * from module_config_element_entry where name like '%motion%';
--  id  | module_id |                 name                  | type | port | display_mode
-- -----+-----------+---------------------------------------+------+------+--------------
--   37 |        18 | Bath room (1-st floor)  motion sensor |    2 |   34 |            1
--   39 |        19 | Corridor motion sensor                |    2 |   35 |            1
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (110, 2, 'Box room Light control Motion Sensor', 2, 86, 1);
