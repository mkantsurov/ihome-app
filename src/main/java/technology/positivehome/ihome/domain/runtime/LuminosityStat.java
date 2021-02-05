package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.List;

public class LuminosityStat {
    private List<ChartPoint> luminosity = new ArrayList<>();

    public List<ChartPoint> getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(List<ChartPoint> luminosity) {
        this.luminosity = luminosity;
    }
}
