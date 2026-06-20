package technology.positivehome.ihome.model.runtime;

import java.util.List;

public record PressureStat(List<ChartPoint> pressure) {
    public static ChartDataBuilder<PressureStat> builder() {
        return new ChartDataBuilder<>();
    }
}
