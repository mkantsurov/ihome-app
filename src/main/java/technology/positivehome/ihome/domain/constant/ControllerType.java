package technology.positivehome.ihome.domain.constant;

/**
 * Created by maxim on 2/25/23.
 **/
public enum ControllerType {

    UNDEFINED,
    MEGAD,
    USR404;

    public static ControllerType of (int type) {
        return switch (type) {
            case 1 -> MEGAD;
            case 2 -> USR404;
            default -> UNDEFINED;
        };
    }
}
