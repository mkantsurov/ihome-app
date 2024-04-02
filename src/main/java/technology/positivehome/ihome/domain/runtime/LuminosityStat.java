package technology.positivehome.ihome.domain.runtime;

import java.util.List;

public record LuminosityStat(List<ChartPoint> luminosity) {
    public static ChartDataBuilder<LuminosityStat> builder() {
        return new ChartDataBuilder<>();
    }
}
