package technology.positivehome.ihome.domain.shared;

import java.util.ArrayList;
import java.util.List;

public record SystemStatInfo(List<ChartPointInfo> heapMax, List<ChartPointInfo> heapUsage) {

    public static SystemStatInfo newInstance() {
        return new SystemStatInfo(new ArrayList<>(), new ArrayList<>());
    }
}
