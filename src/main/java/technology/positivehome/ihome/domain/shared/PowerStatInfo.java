package technology.positivehome.ihome.domain.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PowerStatInfo {

    private List<ChartPointInfo> power = new ArrayList<>();

    public List<ChartPointInfo> getPower() {
        return power;
    }

    public void setPower(List<ChartPointInfo> power) {
        this.power = power;
    }
}
