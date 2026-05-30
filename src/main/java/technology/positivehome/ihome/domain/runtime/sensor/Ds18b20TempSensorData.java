package technology.positivehome.ihome.domain.runtime.sensor;

/**
 * Created by maxim on 6/30/19.
 **/
public class Ds18b20TempSensorData {

    private double data = .0;

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }
}
