package technology.positivehome.ihome.domain.shared;

import java.util.ArrayList;
import java.util.List;

public record TempStatInfo(List<ChartPointInfo> indoorSf,
                           List<ChartPointInfo> indoorGf,
                           List<ChartPointInfo> outdoor,
                           List<ChartPointInfo> garage) {
public static TempStatInfo newInstance() {
    return new TempStatInfo(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
}
}
