package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.List;

public record OutDoorTempStat(List<ChartPoint> temperature) {
    public static ChartDataBuilder<OutDoorTempStat> builder() {
        return new ChartDataBuilder<>();
    }
}
