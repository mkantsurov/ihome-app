package technology.positivehome.ihome.domain.constant;

/**
 * Created by maxim on 6/25/19.
 **/
public enum IHomePropertyType {

    PROTOCOL,
    HOST,
    PORT,
    PATH,
    WS_PATH,
    START_PAGE,
    LOGIN_PAGE,
    LOGOUT_PAGE,
    DIST_VERSION,
    BUILD_VERSION,
    DB_VER,
    FILE_CACHE_DIR,
    IHOME_DIR,
    EMULATION_MODE;

    public boolean isConfigurableRunTime() {
        switch (this) {
            case PROTOCOL:
            case HOST:
            case PORT:
            case PATH:
            case WS_PATH:
            case START_PAGE:
            case LOGIN_PAGE:
            case LOGOUT_PAGE:
            case DIST_VERSION:
            case BUILD_VERSION:
                return false;
        }
        return true;
    }
}
