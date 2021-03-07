package technology.positivehome.ihome.domain.runtime.module;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 7/13/19.
 **/
public class ModuleEntry extends ModuleSummary {

    private List<BinarySensorData> binarySensorData = new ArrayList<>();
    private List<Ds18b20TempSensorReadingData> temperatureSensorData = new ArrayList<>();
    private List<Dht21TempHumiditySensorReadingData> tempHumiditySensorData = new ArrayList<>();
    private List<Bme280TempHumidityPressureSensorReadingData> bme280TempHumidityPressureSensorData = new ArrayList<>();
    private List<Tsl2591LuminositySensorReadingData> tsl2591LuminositySensorData = new ArrayList<>();

    public List<BinarySensorData> getBinarySensorData() {
        return binarySensorData;
    }

    public void setBinarySensorData(List<BinarySensorData> binarySensorData) {
        this.binarySensorData = binarySensorData;
    }

    public List<Ds18b20TempSensorReadingData> getTemperatureSensorData() {
        return temperatureSensorData;
    }

    public void setTemperatureSensorData(List<Ds18b20TempSensorReadingData> temperatureSensorData) {
        this.temperatureSensorData = temperatureSensorData;
    }

    public List<Dht21TempHumiditySensorReadingData> getTempHumiditySensorData() {
        return tempHumiditySensorData;
    }

    public void setTempHumiditySensorData(List<Dht21TempHumiditySensorReadingData> tempHumiditySensorData) {
        this.tempHumiditySensorData = tempHumiditySensorData;
    }

    public List<Bme280TempHumidityPressureSensorReadingData> getBme280TempHumidityPressureSensorData() {
        return bme280TempHumidityPressureSensorData;
    }

    public void setBme280TempHumidityPressureSensorData(List<Bme280TempHumidityPressureSensorReadingData> bme280TempHumidityPressureSensorData) {
        this.bme280TempHumidityPressureSensorData = bme280TempHumidityPressureSensorData;
    }

    public List<Tsl2591LuminositySensorReadingData> getTsl2591LuminositySensorData() {
        return tsl2591LuminositySensorData;
    }

    public void setTsl2591LuminositySensorData(List<Tsl2591LuminositySensorReadingData> tsl2591LuminositySensorData) {
        this.tsl2591LuminositySensorData = tsl2591LuminositySensorData;
    }

}
