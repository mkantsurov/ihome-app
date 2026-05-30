package technology.positivehome.ihome.server.processor;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technology.positivehome.ihome.domain.runtime.ChartPoint;
import technology.positivehome.ihome.domain.runtime.PowerConsumptionStat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by maxim on 3/2/25.
 **/
public class PowerConsumptionStatBuilder {

    private static final Logger log = LoggerFactory.getLogger(PowerConsumptionStatBuilder.class);

    private final List<ChartPoint> extConsumption = new ArrayList<>();
    private final List<ChartPoint> intConsumption = new ArrayList<>();
    private final List<ChartPoint> intBckConsumption = new ArrayList<>();

    public static PowerConsumptionStatBuilder getInstance() {
        return new PowerConsumptionStatBuilder();
    }

    public PowerConsumptionStatBuilder withExtConsumptionChartPoint(ChartPoint chartPoint) {
        extConsumption.add(chartPoint);
        return this;
    }

    public PowerConsumptionStatBuilder withIntConsumptionChartPoint(ChartPoint chartPoint) {
        intConsumption.add(chartPoint);
        return this;
    }

    public PowerConsumptionStatBuilder withIntBckConsumptionChartPoint(ChartPoint chartPoint) {
        intBckConsumption.add(chartPoint);
        return this;
    }

    public PowerConsumptionStat build() {
        extConsumption.sort(Comparator.comparing(ChartPoint::dt));
        intConsumption.sort(Comparator.comparing(ChartPoint::dt));
        intBckConsumption.sort(Comparator.comparing(ChartPoint::dt));
        return new PowerConsumptionStat(calcDiff(extConsumption), calcDiff(intConsumption), calcDiff(intBckConsumption));
    }

    List<ChartPoint> calcDiff(@Nonnull List<ChartPoint> data) {
        data.sort(Comparator.comparing(ChartPoint::dt));
        List<ChartPoint> res = new ArrayList<>();

        ChartPoint startValue = new ChartPoint(LocalDateTime.MIN, -1000);
        int startPoint = 0;
        SortedSet<Integer> valuesWithNormalDiff = new TreeSet<>();
        for (int i = startPoint + 1; i < data.size(); i++) {
            int diff = data.get(i).value() - startValue.value();
            if (diff > 0 && diff < 200) {
                valuesWithNormalDiff.add(data.get(i).value());
                valuesWithNormalDiff.add(startValue.value());
            }
            startValue = data.get(i);
        }
        if (!valuesWithNormalDiff.isEmpty()) {
            Integer minValue = valuesWithNormalDiff.first();
            startValue = new ChartPoint(LocalDateTime.MIN, -1000);
            for (int i = startPoint + 1; i < data.size(); i++) {
                int diff = data.get(i).value() - startValue.value();
                if (startValue.value() - minValue > 1000 || startValue.value() - minValue < 0) {
                    //not expected end value
                    log.warn("Not Expected result of  substitute (startVal): " + diff + "\n    Prev Value: " + startValue + "\n    Current value: " + data.get(i));
                    startValue = data.get(i);
                } else if (data.get(i).value() - minValue > 1000 || data.get(i).value() - minValue < 0) {
                    log.warn("Not Expected result of substitute (curVal): " + diff + "\n    Prev Value: " + startValue + "\n    Current value: " + data.get(i));
                    res.add(new ChartPoint(data.get(i).dt(), 0));
                } else {
                    long startTs = startValue.dt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long endTs = data.get(i).dt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    int interval = (int) Math.round((endTs - startTs) / (1000.0 * 60 * 5));
                    int prevValue = 0;
                    if (!res.isEmpty()) {
                        prevValue = res.get(res.size() - 1).value();
                    }
                    int curValue = (int) Math.round(1000.0 * diff / interval);
                    res.add(new ChartPoint(data.get(i).dt(), (prevValue + curValue) / 2));
                    startValue = data.get(i);
                }
            }
        }
        return res;
    }
}
