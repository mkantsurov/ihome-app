package technology.positivehome.ihome.security.model.permissionproc;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public enum TargetType {

    UNDEFINED,
    MODULE_LIST;

    public static TargetType of(String targetType) {
        if (StringUtils.isNotEmpty(targetType)) {
            String valueToSearch = targetType.trim().toUpperCase(Locale.ROOT).replace("-", "_");
            for (TargetType type : values()) {
                if (type.name().equals(valueToSearch)) {
                    return type;
                }
            }
        }
        return UNDEFINED;
    }
}
