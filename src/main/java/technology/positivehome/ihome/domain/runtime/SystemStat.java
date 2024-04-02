package technology.positivehome.ihome.domain.runtime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record SystemStat(List<ChartPoint> heapMax, List<ChartPoint> heapUsage) {
    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private final List<ChartPoint> heapMaxChart = new ArrayList<>();
        private final List<ChartPoint> heapUsageChart = new ArrayList<>();

        public Builder withChartPoint(LocalDateTime ts, int heapMax, int heapUsage) {
            heapMaxChart.add(ChartPoint.of(ts, heapMax));
            heapUsageChart.add(ChartPoint.of(ts, heapUsage));
            return this;
        }

        public SystemStat build() {
            if (heapMaxChart.isEmpty()) {
                heapMaxChart.add(ChartPoint.of(LocalDateTime.now(), 0));
            }
            if (heapUsageChart.isEmpty()) {
                heapUsageChart.add(ChartPoint.of(LocalDateTime.now(), 0));
            }
            heapMaxChart.sort(Comparator.comparing(ChartPoint::dt));
            heapUsageChart.sort(Comparator.comparing(ChartPoint::dt));
            return new SystemStat(heapMaxChart, heapUsageChart);
        }
    }
}
