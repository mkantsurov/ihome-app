package technology.positivehome.ihome.model.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PowerVoltageExtStatInfo (List<ChartPointInfo> extVoltage) {
}
