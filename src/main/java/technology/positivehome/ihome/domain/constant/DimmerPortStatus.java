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
        if (value > DimmerPortStatus.MAX.ordinal()) {
            return  DimmerPortStatus.MAX;
        }
        int startValue = 0;
        for (DimmerPortStatus status : values()) {
            if (value > startValue && value <= status.ordinal()) {
                return status;
            }
            startValue = status.ordinal();
        }
        return OFF;
    }

    public static DimmerPortStatus ofHwValue(int value) {
        if (value == 0) {
            return OFF;
        }
        if (value > DimmerPortStatus.MAX.hwValue()) {
            return  DimmerPortStatus.MAX;
        }
        int startValue = 0;
        for (DimmerPortStatus status : values()) {
            if (value > startValue && value <= status.hwValue()) {
                return status;
            }
            startValue = status.hwValue();
        }
        return OFF;
    }

    public int hwValue() {
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
