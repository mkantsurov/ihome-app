package technology.positivehome.ihome.domain.runtime.module;

/**
 * Created by maxim on 7/14/19.
 **/
public class Bme280TempHumidityPressureSensorReadingData extends SensorReadingData {

    private double temperature = .0;
    private double humidity = .0;
    private double pressure = .0;

    public Bme280TempHumidityPressureSensorReadingData() {
    }

    public Bme280TempHumidityPressureSensorReadingData(long id, int type, String name, int displayMode, double temperature, double humidity, double pressure) {
        super(id, type, name, displayMode);
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
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

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }
}
