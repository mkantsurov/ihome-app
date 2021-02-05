package technology.positivehome.ihome.domain.runtime.sensor;

/**
 * Created by maxim on 6/30/19.
 **/
public class Dht21TempHumiditySensorData {

    private double temperature = .0;
    private double humidity = .0;

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
}
