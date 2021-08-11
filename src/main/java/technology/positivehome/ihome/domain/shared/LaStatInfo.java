package technology.positivehome.ihome.domain.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 8/11/21.
 **/
public class LaStatInfo {
    private List<ChartPointInfo> la = new ArrayList<>();

    public List<ChartPointInfo> getLa() {
        return la;
    }

    public void setLa(List<ChartPointInfo> la) {
        this.la = la;
    }
}
