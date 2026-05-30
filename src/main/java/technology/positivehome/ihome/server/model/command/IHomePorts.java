package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.domain.runtime.sensor.Dds238PowerMeterData;
import technology.positivehome.ihome.server.service.core.controller.input.*;
import technology.positivehome.ihome.server.service.core.controller.output.DimmerOutput;
import technology.positivehome.ihome.server.service.core.controller.output.RelayOutput;
import technology.positivehome.mgr.processor.megad.controller.input.BinarySensor;

import java.util.Collections;
import java.util.Map;

public record IHomePorts(
        Map<Long, RelayOutput> relayPorts,
        Map<Long, BinarySensor> binarySensors,
        Map<Long, DimmerOutput> dimmerPorts,
        Map<Long, Ds18b20TempSensor> ds1820SensorPorts,
        Map<Long, Dht21TempHumiditySensor> dht21SensorPorts,
        Map<Long, Bme280TempHumidityPressureSensor> bme280SensorPorts,
        Map<Long, Tsl2591LuminositySensor> tsl2591LuminositySensorPorts,
        Map<Long, ADCConnectedSensor> adcConnectedSensorPorts,
        Map<Long, Dds238PowerMeter> dds238PowerMeterPorts) {
    /**
     * Initialized for megad controller
     * @param relayPorts
     * @param binarySensors
     * @param dimmerPorts
     * @param ds1820SensorPorts
     * @param dht21SensorPorts
     * @param bme280SensorPorts
     * @param tsl2591LuminositySensorPorts
     * @param adcConnectedSensorPorts
     * @return
     */
    public static IHomePorts of(Map<Long, RelayOutput> relayPorts, Map<Long, BinarySensor> binarySensors, Map<Long, DimmerOutput> dimmerPorts, Map<Long, Ds18b20TempSensor> ds1820SensorPorts, Map<Long, Dht21TempHumiditySensor> dht21SensorPorts, Map<Long, Bme280TempHumidityPressureSensor> bme280SensorPorts, Map<Long, Tsl2591LuminositySensor> tsl2591LuminositySensorPorts, Map<Long, ADCConnectedSensor> adcConnectedSensorPorts) {
        return new IHomePorts(
                Collections.unmodifiableMap(relayPorts),
                Collections.unmodifiableMap(binarySensors),
                Collections.unmodifiableMap(dimmerPorts),
                Collections.unmodifiableMap(ds1820SensorPorts),
                Collections.unmodifiableMap(dht21SensorPorts),
                Collections.unmodifiableMap(bme280SensorPorts),
                Collections.unmodifiableMap(tsl2591LuminositySensorPorts),
                Collections.unmodifiableMap(adcConnectedSensorPorts),
                null);
    }

    /**
     * Initializer for DR404 controller
     * @param dds238PowerMeterPorts
     * @return
     */
    public static IHomePorts of(Map<Long, Dds238PowerMeter> dds238PowerMeterPorts) {
        return new IHomePorts(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Collections.unmodifiableMap(dds238PowerMeterPorts));
    }
}
