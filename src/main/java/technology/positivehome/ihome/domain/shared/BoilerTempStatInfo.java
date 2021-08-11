package technology.positivehome.ihome.domain.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoilerTempStatInfo {

    private List<ChartPointInfo> temperature = new ArrayList<>();

    public List<ChartPointInfo> getTemperature() {
        return temperature;
    }

    public void setTemperature(List<ChartPointInfo> temperature) {
        this.temperature = temperature;
    }
}
