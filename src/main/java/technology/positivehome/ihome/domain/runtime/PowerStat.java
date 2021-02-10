package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 8/17/19.
 **/
public class PowerStat {

    private List<ChartPoint> power = new ArrayList<>();

    public List<ChartPoint> getPower() {
        return power;
    }

    public void setPower(List<ChartPoint> power) {
        this.power = power;
    }
}
