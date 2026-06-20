package technology.positivehome.ihome.model.runtime;

import java.util.List;

public record LaStat(List<ChartPoint> la) {
    public static ChartDataBuilder<LaStat> builder() {
        return new ChartDataBuilder<>();
    }
}
