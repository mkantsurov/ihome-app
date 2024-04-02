package technology.positivehome.ihome.domain.runtime;

import java.util.List;

public record BoilerTempStat(List<ChartPoint> temperature) {
    public static ChartDataBuilder<BoilerTempStat> builder() {
        return new ChartDataBuilder<>();
    }
}
