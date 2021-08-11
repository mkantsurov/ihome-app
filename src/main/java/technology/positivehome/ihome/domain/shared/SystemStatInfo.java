package technology.positivehome.ihome.domain.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 8/11/21.
 **/
public class SystemStatInfo {
    private List<ChartPointInfo> heapMax = new ArrayList<>();
    private List<ChartPointInfo> heapUsage = new ArrayList<>();

    public List<ChartPointInfo> getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(List<ChartPointInfo> heapMax) {
        this.heapMax = heapMax;
    }

    public List<ChartPointInfo> getHeapUsage() {
        return heapUsage;
    }

    public void setHeapUsage(List<ChartPointInfo> heapUsage) {
        this.heapUsage = heapUsage;
    }
}
