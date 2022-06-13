package technology.positivehome.ihome.domain.runtime.module;

import technology.positivehome.ihome.domain.constant.ModuleAssignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 7/13/19.
 **/
public class ModuleEntry extends ModuleSummary {

    private final List<BinarySensorData> binarySensorData = new ArrayList<>();
    private final List<Ds18b20TempSensorReadingData> temperatureSensorData = new ArrayList<>();
    private final List<Dht21TempHumiditySensorReadingData> tempHumiditySensorData = new ArrayList<>();
    private final List<Bme280TempHumidityPressureSensorReadingData> bme280TempHumidityPressureSensorData = new ArrayList<>();
    private final List<Tsl2591LuminositySensorReadingData> tsl2591LuminositySensorData = new ArrayList<>();

    public ModuleEntry(long moduleId, String name, int mode, int startupMode, int outputPortState, ModuleAssignment assignment, long group) {
        super(moduleId, name, mode, startupMode, outputPortState, assignment, group);
    }

    public List<BinarySensorData> getBinarySensorData() {
        return binarySensorData;
    }

    public List<Ds18b20TempSensorReadingData> getTemperatureSensorData() {
        return temperatureSensorData;
    }

    public List<Dht21TempHumiditySensorReadingData> getTempHumiditySensorData() {
        return tempHumiditySensorData;
    }

    public List<Bme280TempHumidityPressureSensorReadingData> getBme280TempHumidityPressureSensorData() {
        return bme280TempHumidityPressureSensorData;
    }

    public List<Tsl2591LuminositySensorReadingData> getTsl2591LuminositySensorData() {
        return tsl2591LuminositySensorData;
    }

}
