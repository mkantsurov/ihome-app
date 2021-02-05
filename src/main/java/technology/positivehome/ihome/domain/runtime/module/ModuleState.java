package technology.positivehome.ihome.domain.runtime.module;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Tsl2591LuminositySensorData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maxim on 7/1/19.
 **/
public class ModuleState {

    private OutputPortStatus outputPortStatus;
    private Map<Long, BinaryPortStatus> binarySensorData = new HashMap<>();
    private Map<Long, Ds18b20TempSensorData> temperatureSensorData = new HashMap<>();
    private Map<Long, Dht21TempHumiditySensorData> tempHumiditySensorData = new HashMap<>();
    private Map<Long, Bme280TempHumidityPressureSensorData> bme280TempHumidityPressureSensorData = new HashMap<>();
    private Map<Long, Tsl2591LuminositySensorData> tsl2591LuminositySensorData = new HashMap<>();

    public OutputPortStatus getOutputPortStatus() {
        return outputPortStatus;
    }

    public void setOutputPortStatus(OutputPortStatus outputPortStatus) {
        this.outputPortStatus = outputPortStatus;
    }

    public Map<Long, BinaryPortStatus> getBinarySensorData() {
        return binarySensorData;
    }

    public void setBinarySensorData(Map<Long, BinaryPortStatus> binarySensorData) {
        this.binarySensorData = binarySensorData;
    }

    public Map<Long, Ds18b20TempSensorData> getTemperatureSensorData() {
        return temperatureSensorData;
    }

    public void setTemperatureSensorData(Map<Long, Ds18b20TempSensorData> temperatureSensorData) {
        this.temperatureSensorData = temperatureSensorData;
    }

    public Map<Long, Dht21TempHumiditySensorData> getTempHumiditySensorData() {
        return tempHumiditySensorData;
    }

    public void setTempHumiditySensorData(Map<Long, Dht21TempHumiditySensorData> tempHumiditySensorData) {
        this.tempHumiditySensorData = tempHumiditySensorData;
    }

    public Map<Long, Bme280TempHumidityPressureSensorData> getBme280TempHumidityPressureSensorData() {
        return bme280TempHumidityPressureSensorData;
    }

    public void setBme280TempHumidityPressureSensorData(Map<Long, Bme280TempHumidityPressureSensorData> bme280TempHumidityPressureSensorData) {
        this.bme280TempHumidityPressureSensorData = bme280TempHumidityPressureSensorData;
    }

    public Map<Long, Tsl2591LuminositySensorData> getTsl2591LuminositySensorData() {
        return tsl2591LuminositySensorData;
    }

    public void setTsl2591LuminositySensorData(Map<Long, Tsl2591LuminositySensorData> tsl2591LuminositySensorData) {
        this.tsl2591LuminositySensorData = tsl2591LuminositySensorData;
    }

}
