package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 8/17/19.
 **/
public class PowerConsumptionStat {
    private List<ChartPoint> extConsumption = new ArrayList<>();
    private List<ChartPoint> intConsumption = new ArrayList<>();

    public List<ChartPoint> getIntConsumption() {
        return intConsumption;
    }

    public void setIntConsumption(List<ChartPoint> intConsumption) {
        this.intConsumption = intConsumption;
    }

    public List<ChartPoint> getExtConsumption() {
        return extConsumption;
    }

    public void setExtConsumption(List<ChartPoint> extConsumption) {
        this.extConsumption = extConsumption;
    }

}
