package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 8/17/19.
 **/
public class OutDoorTempStat {

    private List<ChartPoint> temperature = new ArrayList<>();

    public List<ChartPoint> getTemperature() {
        return temperature;
    }

    public void setTemperature(List<ChartPoint> temperature) {
        this.temperature = temperature;
    }
}
