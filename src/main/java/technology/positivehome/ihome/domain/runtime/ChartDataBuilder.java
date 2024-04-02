package technology.positivehome.ihome.domain.runtime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class ChartDataBuilder<R> {
    List<ChartPoint> chartData = new ArrayList<>();

    public ChartDataBuilder withChartPoint(ChartPoint cp) {
        chartData.add(cp);
        return this;
    }

    public R build(Function<List<ChartPoint>, R> function) {
        if (chartData.isEmpty()) {
            chartData.add(ChartPoint.of(LocalDateTime.now(), 0));
        }
        chartData.sort(Comparator.comparing(ChartPoint::dt));
        return function.apply(chartData);
    }
}
