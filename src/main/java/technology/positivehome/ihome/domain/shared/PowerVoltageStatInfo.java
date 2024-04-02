package technology.positivehome.ihome.domain.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PowerVoltageStatInfo(List<ChartPointInfo> extVoltage,
                                   List<ChartPointInfo> intVoltage,
                                   List<ChartPointInfo> intBckVoltage) {
    public static PowerVoltageStatInfo newInstance() {
        return new PowerVoltageStatInfo(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
