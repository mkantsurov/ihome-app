package technology.positivehome.ihome.security.model.permissionproc;


import java.util.Locale;

public enum EntityAccessPermission {
    UNDEFINED,
    READ,
    WRITE,
    CREATE,
    DELETE;

    public static EntityAccessPermission of(Object permission) {
        if (permission instanceof String) {
            String valueToSearch = ((String) permission).trim().toUpperCase(Locale.ROOT);
            for (EntityAccessPermission perm : EntityAccessPermission.values()) {
                if (perm.name().equals(valueToSearch)) {
                    return perm;
                }
            }
        }
        return UNDEFINED;
    }
}
