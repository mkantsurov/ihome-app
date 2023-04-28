package technology.positivehome.ihome.domain.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PowerConsumptionStatInfo {

    private List<ChartPointInfo> extConsumption = new ArrayList<>();
    private List<ChartPointInfo> intConsumption = new ArrayList<>();

    public List<ChartPointInfo> getExtConsumption() {
        return extConsumption;
    }

    public void setExtConsumption(List<ChartPointInfo> extConsumption) {
        this.extConsumption = extConsumption;
    }

    public List<ChartPointInfo> getIntConsumption() {
        return intConsumption;
    }

    public void setIntConsumption(List<ChartPointInfo> intConsumption) {
        this.intConsumption = intConsumption;
    }
}
