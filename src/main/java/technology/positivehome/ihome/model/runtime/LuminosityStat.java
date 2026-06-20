package technology.positivehome.ihome.model.runtime;

import java.util.List;

public record LuminosityStat(List<ChartPoint> luminosity) {
    public static ChartDataBuilder<LuminosityStat> builder() {
        return new ChartDataBuilder<>();
    }
}
