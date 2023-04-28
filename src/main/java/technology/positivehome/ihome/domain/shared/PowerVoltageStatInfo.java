package technology.positivehome.ihome.domain.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PowerVoltageStatInfo {

    private List<ChartPointInfo> extVoltage = new ArrayList<>();
    private List<ChartPointInfo> intVoltage = new ArrayList<>();

    public List<ChartPointInfo> getExtVoltage() {
        return extVoltage;
    }

    public void setExtVoltage(List<ChartPointInfo> extVoltage) {
        this.extVoltage = extVoltage;
    }

    public List<ChartPointInfo> getIntVoltage() {
        return intVoltage;
    }

    public void setIntVoltage(List<ChartPointInfo> intVoltage) {
        this.intVoltage = intVoltage;
    }
}
