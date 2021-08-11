package technology.positivehome.ihome.domain.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PressureStatInfo {

    private List<ChartPointInfo> pressure = new ArrayList<>();

    public List<ChartPointInfo> getPressure() {
        return pressure;
    }

    public void setPressure(List<ChartPointInfo> pressure) {
        this.pressure = pressure;
    }
}
