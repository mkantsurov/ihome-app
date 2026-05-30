package technology.positivehome.ihome.server.processor;

import technology.positivehome.ihome.domain.shared.ChartPointInfo;

import java.util.ArrayList;
import java.util.List;

public record PowerConsumptionStatInfo(List<ChartPointInfo> extConsumption,
                                       List<ChartPointInfo> intConsumption,
                                       List<ChartPointInfo> intBckConsumption) {
    public static PowerConsumptionStatInfo newInstance() {
        return new PowerConsumptionStatInfo(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
