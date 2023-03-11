package technology.positivehome.ihome.domain.runtime.module;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.constant.DimmerPortStatus;

import java.util.Iterator;
import java.util.List;

public record OutputPortStatus(int value) {
    public static OutputPortStatus undefined() {
        return new OutputPortStatus(-1);
    }

    public static OutputPortStatus disabled() {
        return new OutputPortStatus(DimmerPortStatus.OFF.ordinal());
    }

    public static OutputPortStatus enabled() {
        return new OutputPortStatus(DimmerPortStatus.MAX.ordinal());
    }

    public static OutputPortStatus of(DimmerPortStatus status) {
        return new OutputPortStatus(status.ordinal());
    }

    public static OutputPortStatus of(BinaryPortStatus status) {
        return switch (status) {
            case DISABLED -> OutputPortStatus.disabled();
            case ENABLED -> OutputPortStatus.enabled();
            default -> OutputPortStatus.undefined();
        };
    }

    public static OutputPortStatus of(boolean dimmableOutput, int outputValue) {
        if (dimmableOutput) {
            return OutputPortStatus.of(DimmerPortStatus.of(outputValue));
        } else {
            return outputValue > 0 ? OutputPortStatus.enabled() : OutputPortStatus.disabled();
        }
    }

    public static OutputPortStatus summarize(List<OutputPortStatus> statusList) {
        if (statusList.isEmpty()) {
            return OutputPortStatus.undefined();
        }

        if (statusList.size() == 1) {
            return new OutputPortStatus(statusList.get(0).value());
        }

        Iterator<OutputPortStatus> it = statusList.iterator();

        int resStatus = it.next().value();
        while (it.hasNext()) {
            OutputPortStatus nextStatus = it.next();
            if (resStatus < 0 || nextStatus.isUndefined()) {
                return OutputPortStatus.undefined();
            }
            if (resStatus > 0 && !nextStatus.isEnabled()) {
                return OutputPortStatus.undefined();
            }
            if (resStatus == 0 && nextStatus.isEnabled()) {
                return OutputPortStatus.undefined();
            }
            resStatus = resStatus + nextStatus.value() / 2;
        }
        return new OutputPortStatus(DimmerPortStatus.of(resStatus).ordinal());
    }

    public boolean isEnabled() {
        return value > 0;
    }

    public boolean isUndefined() {
        return value < 0;
    }

    public boolean isDisabled() {
        return value == 0;
    }
}
