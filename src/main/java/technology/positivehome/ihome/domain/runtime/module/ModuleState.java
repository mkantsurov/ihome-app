package technology.positivehome.ihome.domain.runtime.module;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.sensor.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maxim on 7/1/19.
 **/
public class ModuleState {
    private OutputPortStatus outputPortStatus;
    private final Map<Long, BinaryPortStatus> binarySensorData = new HashMap<>();
    private final Map<Long, Ds18b20TempSensorData> temperatureSensorData = new HashMap<>();
    private final Map<Long, Dht21TempHumiditySensorData> tempHumiditySensorData = new HashMap<>();
    private final Map<Long, Bme280TempHumidityPressureSensorData> bme280TempHumidityPressureSensorData = new HashMap<>();
    private final Map<Long, Tsl2591LuminositySensorData> tsl2591LuminositySensorData = new HashMap<>();
    private final Map<Long, Dds238PowerMeterData> dds238PowerMeterData = new HashMap<>();
    public OutputPortStatus getOutputPortStatus() {
        return outputPortStatus;
    }

    public void setOutputPortStatus(OutputPortStatus outputPortStatus) {
        this.outputPortStatus = outputPortStatus;
    }

    public Map<Long, BinaryPortStatus> getBinarySensorData() {
        return binarySensorData;
    }

    public Map<Long, Ds18b20TempSensorData> getTemperatureSensorData() {
        return temperatureSensorData;
    }

    public Map<Long, Dht21TempHumiditySensorData> getTempHumiditySensorData() {
        return tempHumiditySensorData;
    }

    public Map<Long, Bme280TempHumidityPressureSensorData> getBme280TempHumidityPressureSensorData() {
        return bme280TempHumidityPressureSensorData;
    }

    public Map<Long, Tsl2591LuminositySensorData> getTsl2591LuminositySensorData() {
        return tsl2591LuminositySensorData;
    }

    public Map<Long, Dds238PowerMeterData> getDds238PowerMeterData() {
        return dds238PowerMeterData;
    }

}
