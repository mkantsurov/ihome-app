package technology.positivehome.ihome.domain.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuminosityStatInfo {

    private List<ChartPointInfo> luminosity = new ArrayList<>();

    public List<ChartPointInfo> getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(List<ChartPointInfo> luminosity) {
        this.luminosity = luminosity;
    }
}
