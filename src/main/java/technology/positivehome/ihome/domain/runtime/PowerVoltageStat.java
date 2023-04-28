package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 8/17/19.
 **/
public class PowerVoltageStat {
    private List<ChartPoint> extVoltage = new ArrayList<>();
    private List<ChartPoint> intVoltage = new ArrayList<>();

    public List<ChartPoint> getIntVoltage() {
        return intVoltage;
    }

    public void setIntVoltage(List<ChartPoint> intVoltage) {
        this.intVoltage = intVoltage;
    }

    public List<ChartPoint> getExtVoltage() {
        return extVoltage;
    }

    public void setExtVoltage(List<ChartPoint> extVoltage) {
        this.extVoltage = extVoltage;
    }

}
