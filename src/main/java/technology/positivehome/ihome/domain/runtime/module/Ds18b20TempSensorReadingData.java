package technology.positivehome.ihome.domain.runtime.module;

/**
 * Created by maxim on 7/14/19.
 **/
public class Ds18b20TempSensorReadingData extends SensorReadingData {

    private double data = .0;

    public Ds18b20TempSensorReadingData() {
    }

    public Ds18b20TempSensorReadingData(long id, int type, String name, int displayMode, double data) {
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
