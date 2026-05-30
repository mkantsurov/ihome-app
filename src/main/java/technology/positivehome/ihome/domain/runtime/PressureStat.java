package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 8/17/19.
 **/
public class PressureStat {

    private List<ChartPoint> pressure = new ArrayList<>();

    public List<ChartPoint> getPressure() {
        return pressure;
    }

    public void setPressure(List<ChartPoint> pressure) {
        this.pressure = pressure;
    }
}
