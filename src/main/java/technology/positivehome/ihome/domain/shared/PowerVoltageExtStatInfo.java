package technology.positivehome.ihome.domain.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PowerVoltageExtStatInfo {

    private List<ChartPointInfo> extVoltage = new ArrayList<>();

    public PowerVoltageExtStatInfo() {}
    public PowerVoltageExtStatInfo(List<ChartPointInfo> extVoltage) {
        this.extVoltage = extVoltage;
    }

    public List<ChartPointInfo> getExtVoltage() {
        return extVoltage;
    }

    public void setExtVoltage(List<ChartPointInfo> extVoltage) {
        this.extVoltage = extVoltage;
    }

}
