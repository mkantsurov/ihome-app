package technology.positivehome.ihome.domain.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 8/17/19.
 **/
public class TempStat {

    private List<ChartPoint> indoor = new ArrayList<>();
    private List<ChartPoint> indoorGf = new ArrayList<>();
    private List<ChartPoint> outdoor = new ArrayList<>();
    private List<ChartPoint> garage = new ArrayList<>();

    public List<ChartPoint> getIndoor() {
        return indoor;
    }

    public void setIndoor(List<ChartPoint> indoor) {
        this.indoor = indoor;
    }

    public List<ChartPoint> getIndoorGf() {
        return indoorGf;
    }

    public void setIndoorGf(List<ChartPoint> indoorGf) {
        this.indoorGf = indoorGf;
    }

    public List<ChartPoint> getOutdoor() {
        return outdoor;
    }

    public void setOutdoor(List<ChartPoint> outdoor) {
        this.outdoor = outdoor;
    }

    public List<ChartPoint> getGarage() {
        return garage;
    }

    public void setGarage(List<ChartPoint> garage) {
        this.garage = garage;
    }

}
