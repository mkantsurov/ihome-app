package technology.positivehome.ihome.domain.runtime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public record TempStat(List<ChartPoint> indoorSf, List<ChartPoint> indoorGf, List<ChartPoint> outdoor, List<ChartPoint> garage) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<ChartPoint> indoorSf = new ArrayList<>();
        private final List<ChartPoint> indoorGf = new ArrayList<>();
        private final List<ChartPoint> outdoor = new ArrayList<>();
        private final List<ChartPoint> garage = new ArrayList<>();

        public Builder withIndoorSfTempChartPoint(ChartPoint cp) {
            this.indoorSf.add(cp);
            return this;
        }

        public Builder withIndoorGfTempChartPoint(ChartPoint cp) {
            this.indoorGf.add(cp);
            return this;
        }

        public Builder withOutDoorTempChartPoint(ChartPoint cp) {
            this.outdoor.add(cp);
            return this;
        }

        public Builder withGarageTempChartPoint(ChartPoint cp) {
            this.garage.add(cp);
            return this;
        }

        public TempStat build() {
            if (indoorSf.isEmpty()) {
                indoorSf.add(ChartPoint.of(LocalDateTime.now(), 0));
            }
            if (indoorGf.isEmpty()) {
                indoorGf.add(ChartPoint.of(LocalDateTime.now(), 0));
            }
            if (outdoor.isEmpty()) {
                outdoor.add(ChartPoint.of(LocalDateTime.now(), 0));
            }
            if (garage.isEmpty()) {
                garage.add(ChartPoint.of(LocalDateTime.now(), 0));
            }
            indoorSf.sort(Comparator.comparing(ChartPoint::dt));
            indoorGf.sort(Comparator.comparing(ChartPoint::dt));
            outdoor.sort(Comparator.comparing(ChartPoint::dt));
            garage.sort(Comparator.comparing(ChartPoint::dt));
            return new TempStat(indoorSf, indoorGf, outdoor, garage);
        }
    }
}
