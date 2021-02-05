package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.List;

public class LaStat {

    private List<ChartPoint> la = new ArrayList<>();

    public List<ChartPoint> getLa() {
        return la;
    }

    public void setLa(List<ChartPoint> la) {
        this.la = la;
    }
}
