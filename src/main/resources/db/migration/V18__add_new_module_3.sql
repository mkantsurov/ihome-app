-- ihome=> select * from controller_port_config_entry where id=13;
--  id | controller_id | port_address | type |     description
-- ----+---------------+--------------+------+---------------------
--  13 |             3 |           23 |    1 | Second flor 220v PS

INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description) VALUES (82, 1, 17, 1, 'Garage 220v PS');

-- ihome=> select * from module_config_entry where id=7;
--  id | mode |                name                | group_id | display | type | module_assignment | startup_mode
-- ----+------+------------------------------------+----------+---------+------+-------------------+--------------
--   7 |    2 | Second Flor 220v PS control module |        3 | t       |    8 |                 1 |            0
-- (1 row)


INSERT INTO module_config_entry (id,mode,name,group_id,display,type,module_assignment,startup_mode) VALUES (43, 2, 'Garage 220v PS control module', 1, true, 8, 1, 0);


-- ihome=> select * from module_config_element_entry where module_id=7;;
--  id | module_id |               name                | type | port | display_mode
-- ----+-----------+-----------------------------------+------+------+--------------
--  14 |         7 | Second Flor 220v PS control relay |    1 |   13 |            1
--  30 |         7 | Input Power Sensor                |    2 |   29 |            1

INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode) VALUES (104, 43, 'Garage 220v PS control relay', 1, 82, 1);
INSERT INTO module_config_element_entry(id, module_id, name, type, port, display_mode) VALUES (105, 43, 'Input Power Sensor', 2, 29, 1);
