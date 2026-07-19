-- Grant SUPERVISOR write access to all remaining light control modules.
--
-- The children room lights (#10, #11, #14, #44) were already given explicit
-- CHILDREN_ROOM1_MANAGER+SUPERVISOR / CHILDREN_ROOM2_MANAGER+SUPERVISOR
-- permissions in V43, so they are excluded here.
--
-- All other light control modules below currently have an empty permission
-- column ([]), which means SUPERVISOR already has write access via the
-- LIGHT_CONTROL assignment fallback.  This migration makes that access
-- *explicit* by writing the permission column, so that SUPERVISOR access
-- is preserved even if the fallback logic is ever changed or removed.
--
-- When the permission column is non-empty, only the listed roles + ADMIN
-- have WRITE access.

-- Garage light control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 2;

-- Exterior light control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 4;

-- Garden light power control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 6;

-- Wardrobe light PS control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 9;

-- Bedroom Light PS control module #2
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 13;

-- Bath Room (1-st floor) Light Control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 18;

-- Corridor Light Control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 19;

-- Kitchen main Light Control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 20;

-- Living room Light Control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 21;

-- Cabinet Light Control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 22;

-- Corridor 2-nd flor light control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 23;

-- Bath Room (2-nd floor) Light Control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 24;

-- Stairwell light control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 28;

-- Bedroom light PS (wall sconces)
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 33;

-- Box room Light control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 36;

-- Kitchen Illumination of the working area Control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 38;

-- Kitchen main Light Control module#2
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 40;

-- Bath Room Second Floor Mirror Light PS control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 45;

-- Bathroom Mirror (2-nd floor) Light control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 46;

-- Bathroom (1-st floor) Mirror Light control module
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 47;
