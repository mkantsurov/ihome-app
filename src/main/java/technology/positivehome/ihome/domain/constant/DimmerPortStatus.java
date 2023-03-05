package technology.positivehome.ihome.domain.constant;

/**
 * Created by maxim on 3/4/23.
 **/
public enum DimmerPortStatus {
    OFF,
    MINIMUM,
    LOW,
    MID,
    HIGH,
    MAX;

    public static DimmerPortStatus of(int value) {
        if (value == 0) {
            return OFF;
        }
        int startValue = 0;
        for (DimmerPortStatus status : values()) {
            if (value > startValue && value <= status.intValue()) {
                return status;
            }
            startValue = status.intValue();
        }
        return OFF;
    }

    public int intValue() {
        return switch (this) {
            case OFF -> 0;
            case MINIMUM -> 40;
            case LOW -> 80;
            case MID -> 123;
            case HIGH -> 200;
            case MAX -> 255;
        };
    }
}
