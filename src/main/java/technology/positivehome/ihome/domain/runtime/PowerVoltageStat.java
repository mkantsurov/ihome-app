package technology.positivehome.ihome.domain.runtime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by maxim on 8/17/19.
 **/
public record PowerVoltageStat(List<ChartPoint> extVoltage, List<ChartPoint> intVoltage,
                               List<ChartPoint> intBckVoltage) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<ChartPoint> extVoltage = new ArrayList<>();
        private final List<ChartPoint> intVoltage = new ArrayList<>();
        private final List<ChartPoint> intBckVoltage = new ArrayList<>();

        public PowerVoltageStat build() {
            if (extVoltage.isEmpty()) {
                extVoltage.add(ChartPoint.of(LocalDateTime.now(), 0));
            }
            if (intVoltage.isEmpty()) {
                intVoltage.add(ChartPoint.of(LocalDateTime.now(), 0));
            }
            if (intBckVoltage.isEmpty()) {
                intBckVoltage.add(ChartPoint.of(LocalDateTime.now(), 0));
            }
            extVoltage.sort(Comparator.comparing(ChartPoint::dt));
            intVoltage.sort(Comparator.comparing(ChartPoint::dt));
            intBckVoltage.sort(Comparator.comparing(ChartPoint::dt));
            return new PowerVoltageStat(extVoltage, intVoltage, intBckVoltage);
        }

        public Builder withExtVoltageChartPoint(ChartPoint chartPoint) {
            extVoltage.add(chartPoint);
            return this;
        }
        public Builder withIntVoltageChartPoint(ChartPoint chartPoint) {
            intVoltage.add(chartPoint);
            return this;
        }
        public Builder withIntBckVoltageChartPoint(ChartPoint chartPoint) {
            intBckVoltage.add(chartPoint);
            return this;
        }

    }
}
