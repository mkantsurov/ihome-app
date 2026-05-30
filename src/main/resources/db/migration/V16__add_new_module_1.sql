--select * from module_property_entry where module_id=26;
-- id | module_id | key | long_value | string_value
-- ----+-----------+-----+------------+--------------
--   11 |        42 |   7 |         30 |             CHECK_BATH_ROOM_HUMIDITY_INTERVAL
--   12 |        42 |   6 |         80 |             BATH_ROOM_HUMIDITY_SENSOR
--   13 |        42 |   5 |         36 |             INDOOR_HUMIDITY_SENSOR
--   14 |        42 |   8 |         34 |             BATH_ROOM_MOTION_SENSOR
-- (4 rows)

INSERT INTO module_property_entry (id, module_id, key, long_value) VALUES (11, 42, 7, 30);
INSERT INTO module_property_entry (id, module_id, key, long_value) VALUES (12, 42, 6, 80);
INSERT INTO module_property_entry (id, module_id, key, long_value) VALUES (13, 42, 5, 36);
INSERT INTO module_property_entry (id, module_id, key, long_value) VALUES (14, 42, 8, 34);

-- ihome=> select * from module_property_entry where module_id = 26;
--  id | module_id | key | long_value | string_value
-- ----+-----------+-----+------------+--------------
--   2 |        26 |   7 |         30 |
--   3 |        26 |   6 |         55 |
--   4 |        26 |   5 |         60 | <-- 36
--   5 |        26 |   8 |         59 |

UPDATE module_property_entry SET long_value = 36 WHERE key = 5 AND id = 4;
