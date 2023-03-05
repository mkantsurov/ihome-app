package technology.positivehome.ihome.domain.runtime.module;

import com.fasterxml.jackson.annotation.JsonIgnore;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.constant.DimmerPortStatus;

import java.util.Iterator;
import java.util.List;

/**
 * Created by maxim on 7/1/19.
 **/
public class OutputPortStatus {

    private int value = 0;

    public OutputPortStatus() {
    }

    public OutputPortStatus(int value) {
        this.value = value;
    }

    public static OutputPortStatus undefined() {
        return new OutputPortStatus(-1);
    }

    public static OutputPortStatus disabled() {
        return new OutputPortStatus(0);
    }

    public static OutputPortStatus enabled() {
        return new OutputPortStatus(255);
    }

    public static OutputPortStatus enabled(int value) {
        return new OutputPortStatus(value);
    }

    public static OutputPortStatus intension(int percentage) {
        return new OutputPortStatus((255 * percentage) / 100);
    }

    public static OutputPortStatus of(DimmerPortStatus status) {
        return new OutputPortStatus(status.intValue());
    }

    public static OutputPortStatus of(BinaryPortStatus status) {
        return switch (status) {
            case DISABLED -> OutputPortStatus.disabled();
            case ENABLED -> OutputPortStatus.enabled();
            default -> OutputPortStatus.undefined();
        };
    }

    @JsonIgnore
    public boolean isEnabled() {
        return value > 0;
    }

    @JsonIgnore
    public boolean isDisabled() {
        return value == 0;
    }

    @JsonIgnore
    public boolean isUndefined() {
        return value < 0;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @JsonIgnore
    public int calcIntension() {
        if (value == 0) {
            return 0;
        } else if (value == 255) {
            return 100;
        } else if (value > 0 && value <= 25) {
            return 10;
        } else if (value > 25 && value <= 76) {
            return 30;
        } else if (value > 76 && value <= 204) {
            return 80;
        } else {
            return 100;
        }
    }

    @JsonIgnore
    public static OutputPortStatus summarize(List<OutputPortStatus> statusList) {

        if (statusList.isEmpty()) {
            return OutputPortStatus.undefined();
        }

        if (statusList.size() == 1) {
            return new OutputPortStatus(statusList.get(0).getValue());
        }

        Iterator<OutputPortStatus> it = statusList.iterator();

        OutputPortStatus resStatus = new OutputPortStatus(it.next().getValue());

        while (it.hasNext()) {
            OutputPortStatus nextStatus = it.next();
            if (resStatus.isUndefined() || nextStatus.isUndefined()) {
                return OutputPortStatus.undefined();
            }
            if (resStatus.isEnabled() && !nextStatus.isEnabled()) {
                return OutputPortStatus.undefined();
            }
            if (!resStatus.isEnabled() && nextStatus.isEnabled()) {
                return OutputPortStatus.undefined();
            }
            resStatus.setValue((resStatus.getValue() + nextStatus.getValue()) / 2);
        }
        return resStatus;
    }

}
