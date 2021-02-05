package technology.positivehome.ihome.domain.runtime.module;

public class Tsl2591LuminositySensorReadingData extends SensorReadingData {
    private double data = .0;

    public Tsl2591LuminositySensorReadingData() {
    }

    public Tsl2591LuminositySensorReadingData(long id, int type, String name, int displayMode, double data) {
        super(id, type, name, displayMode);
        this.data = data;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }
}
