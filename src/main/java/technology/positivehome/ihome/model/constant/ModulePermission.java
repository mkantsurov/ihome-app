/**
 * @deprecated Per-module permissions no longer use READ/WRITE levels.
 * The {@code permissions} field in {@code ModuleConfigEntity} / {@code ModuleConfigEntry}
 * now stores a simple list of role names (strings) that have WRITE access.
 * READ is implicit for all roles and determined by system-wide defaults.
 * This enum is no longer used anywhere and will be removed in a future version.
 */
@Deprecated(since = "2025-07-01", forRemoval = true)
public enum ModulePermission {
    READ,
    WRITE
}
