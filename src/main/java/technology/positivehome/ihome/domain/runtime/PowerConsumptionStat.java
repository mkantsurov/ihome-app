package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by maxim on 8/17/19.
 **/
public record PowerConsumptionStat(List<ChartPoint> extConsumption, List<ChartPoint> intConsumption, List<ChartPoint> intBckConsumption) {
    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private final List<ChartPoint> extConsumption = new ArrayList<>();
        private final List<ChartPoint> intConsumption = new ArrayList<>();
        private final List<ChartPoint> intBckConsumption = new ArrayList<>();

        public PowerConsumptionStat build() {
            extConsumption.sort(Comparator.comparing(ChartPoint::dt));
            intConsumption.sort(Comparator.comparing(ChartPoint::dt));
            intBckConsumption.sort(Comparator.comparing(ChartPoint::dt));
            return new PowerConsumptionStat(extConsumption, intConsumption, intBckConsumption);
        }


        public Builder withExtConsumptionChartPoint(ChartPoint chartPoint) {
            extConsumption.add(chartPoint);
            return this;
        }

        public Builder withIntConsumptionChartPoint(ChartPoint chartPoint) {
            intConsumption.add(chartPoint);
            return this;
        }

        public Builder withIntBckConsumptionChartPoint(ChartPoint chartPoint) {
            intBckConsumption.add(chartPoint);
            return this;
        }
    }
}
