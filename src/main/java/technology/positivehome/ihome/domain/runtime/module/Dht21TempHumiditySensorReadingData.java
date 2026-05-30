package technology.positivehome.ihome.domain.runtime.module;

/**
 * Created by maxim on 7/14/19.
 **/
public class Dht21TempHumiditySensorReadingData extends SensorReadingData {

    private double temperature = .0;
    private double humidity = .0;

    public Dht21TempHumiditySensorReadingData() {
    }

    public Dht21TempHumiditySensorReadingData(long id, int type, String name, int displayMode, double temperature, double humidity) {
        super(id, type, name, displayMode);
        this.temperature = temperature;
        this.humidity = humidity;
    }

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
