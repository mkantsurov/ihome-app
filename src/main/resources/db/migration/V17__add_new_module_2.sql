--  id | module_id | key | long_value | string_value
-- ----+-----------+-----+------------+--------------
--   2 |        26 |   7 |         30 |
--   3 |        26 |   6 |         55 |
--   4 |        26 |   5 |         36 | <-- 60
--   5 |        26 |   8 |         59 |

-- ihome=> select * from module_config_element_entry where module_id in (26, 42) order by module_id, id;
--  id  | module_id |                        name                        | type | port | display_mode
-- -----+-----------+----------------------------------------------------+------+------+--------------
--   52 |        26 | Bath Room (2-nd floor) Ventilation Control Relay   |    1 |   50 |            1
--   55 |        26 | Bath room (2-nd floor) temperature/humidity sensor |    5 |   52 |            1
--   59 |        26 | Bath room (2-nd floor) motion sensor               |    2 |   47 |            1
--   60 |        26 | Pressure humidity temp. sensor                     |    7 |   36 |            1
--  100 |        42 | Bath Room (1-st floor) Ventilation Control Relay   |    1 |   81 |            1
--  101 |        42 | Bath room (1-st floor) temperature/humidity sensor |    5 |   80 |            1
--  102 |        42 | Bath room (1-st floor) motion sensor               |    2 |   34 |            1
--  103 |        42 | Pressure humidity temp. sensor                     |    1 |   76 |            1
-- (8 rows)

UPDATE module_property_entry SET long_value = 60 WHERE key = 5 AND module_id = 26; --INDOOR_HUMIDITY_SENSOR

UPDATE module_property_entry SET long_value = 101 WHERE key = 6 AND module_id=42;  --BATH_ROOM_HUMIDITY_SENSOR
UPDATE module_property_entry SET long_value = 103 WHERE key = 5 AND module_id=42;  --INDOOR_HUMIDITY_SENSOR
UPDATE module_property_entry SET long_value = 102 WHERE key = 8 AND module_id=42;  --BATH_ROOM_MOTION_SENSOR

UPDATE module_config_element_entry SET port=36 WHERE id=103;
