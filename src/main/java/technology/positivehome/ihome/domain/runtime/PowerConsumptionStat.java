package technology.positivehome.ihome.domain.runtime;

import java.util.*;

/**
 * Created by maxim on 8/17/19.
 **/
public record PowerConsumptionStat(List<ChartPoint> extConsumption, List<ChartPoint> intConsumption,
                                   List<ChartPoint> intBckConsumption) {
}
