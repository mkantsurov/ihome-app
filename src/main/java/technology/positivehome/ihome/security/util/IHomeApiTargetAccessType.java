package technology.positivehome.ihome.security.util;

import java.util.Optional;

/**
 * Created by maxim on 2/6/21.
 **/
public enum IHomeApiTargetAccessType {
    UNDEFINED,
    READ,
    WRITE,
    CREATE,
    DELETE;

    public static IHomeApiTargetAccessType of(String name) {
        String normalizedName = Optional.ofNullable(name).orElse("undefined").toUpperCase();
        switch (normalizedName) {
            case "READ":
                return READ;
            case "WRITE":
                return WRITE;
            case "CREATE":
                return CREATE;
            case "DELETE":
                return DELETE;
            default:
                return UNDEFINED;
        }
    }
}
