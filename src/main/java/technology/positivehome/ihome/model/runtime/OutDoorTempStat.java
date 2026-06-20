package technology.positivehome.ihome.model.runtime;

import java.util.List;

public record OutDoorTempStat(List<ChartPoint> temperature) {
    public static ChartDataBuilder<OutDoorTempStat> builder() {
        return new ChartDataBuilder<>();
    }
}
