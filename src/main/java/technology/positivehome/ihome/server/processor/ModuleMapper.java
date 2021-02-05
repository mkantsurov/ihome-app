package technology.positivehome.ihome.server.processor;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.*;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Tsl2591LuminositySensorData;
import technology.positivehome.ihome.server.service.core.module.IHomeModuleSummary;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by maxim on 7/4/19.
 **/
public class ModuleMapper {

    public static ModuleSummary from(IHomeModuleSummary iHomeModule) throws MegadApiMallformedUrlException, IOException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, URISyntaxException, InterruptedException {
        if (iHomeModule == null) {
            return null;
        }
        ModuleSummary res = new ModuleSummary();
        res.setModuleId(iHomeModule.getModuleId());
        res.setName(iHomeModule.getName());
        res.setMode(iHomeModule.getMode().ordinal());
        res.setOutputPortState(iHomeModule.getOutputPortStatus().getValue());
        return res;
    }


    public static ModuleStateData from(IHomeModuleSummary moduleSummary, ModuleState moduleState) throws MegadApiMallformedUrlException, IOException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, URISyntaxException, InterruptedException {

        ModuleStateData data = new ModuleStateData();
        data.setModuleId(moduleSummary.getModuleId());
        data.setMode(moduleSummary.getMode().ordinal());
        data.setName(moduleSummary.getName());
        data.setOutputPortState(moduleSummary.getOutputPortStatus().getValue());

        for (ModuleConfigElementEntry entry : moduleSummary.getInputPorts()) {
            switch (entry.getType()) {
                case DIMMER:
                    break;
                case BUTTON:
                case REED_SWITCH:
                    data.getBinarySensorData().add(from(entry, moduleState.getBinarySensorData().get(entry.getId())));
                    break;
                case DS18B20_TEMP_SENSOR:
                    data.getTemperatureSensorData().add(from(entry, moduleState.getTemperatureSensorData().get(entry.getId())));
                    break;
                case DHT21_TEMP_HUMIDITY_SENSOR:
                    data.getTempHumiditySensorData().add(from(entry, moduleState.getTempHumiditySensorData().get(entry.getId())));
                    break;
                case BME280_TEMP_HUMIDITY_PRESS_SENSOR:
                    data.getBme280TempHumidityPressureSensorData().add(from(entry, moduleState.getBme280TempHumidityPressureSensorData().get(entry.getId())));
                    break;
                case TSL2591_LUMINOSITY_SENSOR:
                    data.getTsl2591LuminositySensorData().add(from(entry, moduleState.getTsl2591LuminositySensorData().get(entry.getId())));
                    break;
            }
        }
        return data;
    }

    private static Bme280TempHumidityPressureSensorReadingData from(ModuleConfigElementEntry entry, Bme280TempHumidityPressureSensorData bme280TempHumidityPressureSensorData) {
        return new Bme280TempHumidityPressureSensorReadingData(entry.getId(), entry.getType().ordinal(), entry.getName(),
                entry.getDisplayMode().ordinal(),
                bme280TempHumidityPressureSensorData.getTemperature(),
                bme280TempHumidityPressureSensorData.getHumidity(),
                bme280TempHumidityPressureSensorData.getPressure());
    }

    private static Dht21TempHumiditySensorReadingData from(ModuleConfigElementEntry entry, Dht21TempHumiditySensorData dht21TempHumiditySensorData) {
        return new Dht21TempHumiditySensorReadingData(entry.getId(), entry.getType().ordinal(), entry.getName(),
                entry.getDisplayMode().ordinal(), dht21TempHumiditySensorData.getTemperature(), dht21TempHumiditySensorData.getHumidity());
    }

    private static Ds18b20TempSensorReadingData from(ModuleConfigElementEntry entry, Ds18b20TempSensorData ds18b20TempSensorData) {
        return new Ds18b20TempSensorReadingData(entry.getId(), entry.getType().ordinal(), entry.getName(), entry.getDisplayMode().ordinal(), ds18b20TempSensorData.getData());
    }

    private static Tsl2591LuminositySensorReadingData from(ModuleConfigElementEntry entry, Tsl2591LuminositySensorData tsl2591LuminositySensorData) {
        return new Tsl2591LuminositySensorReadingData(entry.getId(), entry.getType().ordinal(), entry.getName(), entry.getDisplayMode().ordinal(), tsl2591LuminositySensorData.getData());
    }

    private static BinarySensorData from(ModuleConfigElementEntry entry, BinaryPortStatus binaryPortStatus) {
        return new BinarySensorData(entry.getId(), entry.getType().ordinal(), entry.getName(), entry.getDisplayMode().ordinal(), BinaryPortStatus.ENABLED.equals(binaryPortStatus));
    }

}
