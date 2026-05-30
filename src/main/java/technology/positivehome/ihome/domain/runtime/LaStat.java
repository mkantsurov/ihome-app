package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.List;

public record LaStat(List<ChartPoint> la) {
    public static ChartDataBuilder<LaStat> builder() {
        return new ChartDataBuilder<>();
    }
}
