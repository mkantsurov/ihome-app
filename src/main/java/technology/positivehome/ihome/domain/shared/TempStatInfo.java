package technology.positivehome.ihome.domain.shared;

import java.util.ArrayList;
import java.util.List;

public class TempStatInfo {

    private List<ChartPointInfo> indoor = new ArrayList<>();
    private List<ChartPointInfo> indoorGf = new ArrayList<>();
    private List<ChartPointInfo> outdoor = new ArrayList<>();
    private List<ChartPointInfo> garage = new ArrayList<>();

    public List<ChartPointInfo> getIndoor() {
        return indoor;
    }

    public void setIndoor(List<ChartPointInfo> indoor) {
        this.indoor = indoor;
    }

    public List<ChartPointInfo> getIndoorGf() {
        return indoorGf;
    }

    public void setIndoorGf(List<ChartPointInfo> indoorGf) {
        this.indoorGf = indoorGf;
    }

    public List<ChartPointInfo> getOutdoor() {
        return outdoor;
    }

    public void setOutdoor(List<ChartPointInfo> outdoor) {
        this.outdoor = outdoor;
    }

    public List<ChartPointInfo> getGarage() {
        return garage;
    }

    public void setGarage(List<ChartPointInfo> garage) {
        this.garage = garage;
    }

}
