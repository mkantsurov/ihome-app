package technology.positivehome.ihome.security.util;

import java.util.Optional;

/**
 * Created by maxim on 2/6/21.
 **/
public enum IHomeApiTargetType {
    UNDEFINED,
    USER;

    public static IHomeApiTargetType of(String targetType) {

        String normalizedName = Optional.ofNullable(targetType).orElse("undefined").toUpperCase();
        switch (normalizedName) {
            case "USER":
                return USER;
            default:
                return UNDEFINED;
        }
    }
}
