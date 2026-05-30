package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.List;

public class SystemStat {

    private List<ChartPoint> heapMax = new ArrayList<>();
    private List<ChartPoint> heapUsage = new ArrayList<>();

    public List<ChartPoint> getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(List<ChartPoint> heapMax) {
        this.heapMax = heapMax;
    }

    public List<ChartPoint> getHeapUsage() {
        return heapUsage;
    }

    public void setHeapUsage(List<ChartPoint> heapUsage) {
        this.heapUsage = heapUsage;
    }
}
