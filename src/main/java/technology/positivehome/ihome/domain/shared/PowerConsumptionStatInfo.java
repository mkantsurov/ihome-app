package technology.positivehome.ihome.domain.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PowerConsumptionStatInfo(List<ChartPointInfo> extConsumption,
                                       List<ChartPointInfo> intConsumption,
                                       List<ChartPointInfo> intBckConsumption) {
    public static PowerConsumptionStatInfo newInstance() {
        return new PowerConsumptionStatInfo(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
