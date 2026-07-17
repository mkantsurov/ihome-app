package technology.positivehome.ihome.model.runtime;

import java.util.List;

public record BoilerTempStat(List<ChartPoint> temperature) {
    public static ChartDataBuilder<BoilerTempStat> builder() {
        return new ChartDataBuilder<>();
    }
}
