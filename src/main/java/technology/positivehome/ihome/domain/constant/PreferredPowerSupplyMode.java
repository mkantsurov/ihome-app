package technology.positivehome.ihome.domain.constant;

public enum PreferredPowerSupplyMode {
    UNDEFINED,
    DIRECT,
    CONVERTER,
    ONLY_LED;

    public boolean oneOf(PreferredPowerSupplyMode... modes) {
        if (modes == null) {
            return false;
        }
        for (PreferredPowerSupplyMode mode : modes) {
            if (this.equals(mode)) {
                return true;
            }
        }
        return false;
    }

    public boolean noneOf(PreferredPowerSupplyMode... modes) {
        if (modes == null) {
            return true;
        }
        for (PreferredPowerSupplyMode mode : modes) {
            if (this.equals(mode)) {
                return false;
            }
        }
        return true;
    }
}
