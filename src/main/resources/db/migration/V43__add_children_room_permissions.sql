-- Grant CHILDREN_ROOM1_MANAGER and SUPERVISOR write access to Children room #1 lights
-- Grant CHILDREN_ROOM2_MANAGER and SUPERVISOR write access to Children room #2 lights
--
-- When the permission column is non-empty, only the listed roles + ADMIN have WRITE access.
-- SUPERVISOR must be explicitly included here because the explicit whitelist overrides
-- the default fallback (which otherwise grants SUPERVISOR write access to LIGHT_CONTROL modules).
--
-- Also grant SUPERVISOR explicit write access to Garage doors and Sliding Gates modules.
-- These modules currently have an empty permission column, meaning SUPERVISOR already
-- has write access via the GATE_CONTROL assignment fallback. Setting an explicit
-- permission column here makes it explicit and ensures it's not lost if the fallback
-- logic changes in the future.

-- Children room #1 Light PS control module #1 (CHILDREN_ROOM1_MANAGER + SUPERVISOR)
UPDATE module_config_entry
SET permission = '["CHILDREN_ROOM1_MANAGER", "SUPERVISOR"]'::jsonb
WHERE id = 10;

-- Children room #1 Light PS control module #2 (CHILDREN_ROOM1_MANAGER + SUPERVISOR)
UPDATE module_config_entry
SET permission = '["CHILDREN_ROOM1_MANAGER", "SUPERVISOR"]'::jsonb
WHERE id = 11;

-- Children room #2 Light PS control module #1 (CHILDREN_ROOM2_MANAGER + SUPERVISOR)
UPDATE module_config_entry
SET permission = '["CHILDREN_ROOM2_MANAGER", "SUPERVISOR"]'::jsonb
WHERE id = 14;

-- Children room #2 Light PS control module #2 (CHILDREN_ROOM2_MANAGER + SUPERVISOR)
UPDATE module_config_entry
SET permission = '["CHILDREN_ROOM2_MANAGER", "SUPERVISOR"]'::jsonb
WHERE id = 44;

-- Garage doors power control module (SUPERVISOR only)
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 1;

-- Sliding Gates power control module (SUPERVISOR only)
UPDATE module_config_entry
SET permission = '["SUPERVISOR"]'::jsonb
WHERE id = 39;
