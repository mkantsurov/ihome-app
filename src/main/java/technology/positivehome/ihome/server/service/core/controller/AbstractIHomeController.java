package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import technology.positivehome.ihome.domain.constant.MegadPortType;
import technology.positivehome.ihome.domain.runtime.controller.ControllerPortConfigEntry;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.server.service.core.controller.input.*;
import technology.positivehome.ihome.server.service.core.controller.output.DimmerOutput;
import technology.positivehome.ihome.server.service.core.controller.output.RelayOutput;
import technology.positivehome.mgr.processor.megad.controller.input.BinarySensor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by maxim on 6/30/19.
 **/
public abstract class AbstractIHomeController implements IHomeController {
    private static final Log log = LogFactory.getLog(AbstractIHomeController.class);

    private final Map<Long, RelayOutput> relayPorts = new ConcurrentHashMap<>();
    private final Map<Long, DimmerOutput> dimmerPorts = new ConcurrentHashMap<>();
    private final Map<Long, BinarySensor> binarySensors = new ConcurrentHashMap<>();
    private final Map<Long, Ds18b20TempSensor> ds1820SensorPorts = new ConcurrentHashMap<>();
    private final Map<Long, Dht21TempHumiditySensor> dht21SensorPorts = new ConcurrentHashMap<>();
    private final Map<Long, Bme280TempHumidityPressureSensor> bme280SensorPorts = new ConcurrentHashMap<>();
    private final Map<Long, Tsl2591LuminositySensor> tsl2591LuminositySensorPorts = new ConcurrentHashMap<>();
    private final Map<Long, ADCConnectedSensor> adcConnectedSensorPorts = new ConcurrentHashMap<>();
    private final Map<Integer, PortInfo> portInfoByAddress = new ConcurrentHashMap<>();
    private final String moduleUrl;


    private final ApplicationEventPublisher eventPublisher;

    public AbstractIHomeController(ApplicationEventPublisher eventPublisher, String ipAddress, List<ControllerPortConfigEntry> portConfig) {
        this.eventPublisher = eventPublisher;
        this.moduleUrl = "http://" + ipAddress + "/sec/";
        for (ControllerPortConfigEntry configEntry : portConfig) {
            portInfoByAddress.put(configEntry.getPortAdress(), new PortInfo(configEntry.getId(), configEntry.getType()));
            addPort(configEntry);
        }
    }

    @Override
    public void addPort(ControllerPortConfigEntry configEntry) {

        switch (configEntry.getType()) {
            case RELAY_OUTPUT:
                relayPorts.put(configEntry.getId(), createRelayOutput(configEntry.getPortAdress()));
                break;
            case DIMMER_OUTPUT:
                dimmerPorts.put(configEntry.getId(), createDimmerOutput(configEntry.getPortAdress()));
                break;
            case BINARY_INPUT:
                binarySensors.put(configEntry.getId(), createBinarySensor(configEntry.getPortAdress()));
                break;
            case DS1820_TEMPERATURE_SENSOR:
                ds1820SensorPorts.put(configEntry.getId(), createDs1820TemperatureSensor(configEntry.getPortAdress()));
                break;
            case DHT21_TEMPERATURE_HUMIDITY_SENSOR:
                dht21SensorPorts.put(configEntry.getId(), createDht21TemperatureHumiditySensor(configEntry.getPortAdress()));
                break;
            case BME280_TEMP_HUMIDITY_PRESS_SENSOR:
                bme280SensorPorts.put(configEntry.getId(), createBme280TempHumidityPressureSensor(configEntry.getPortAdress()));
                break;
            case TSL2591_LUMINOSITY_SENSOR:
                tsl2591LuminositySensorPorts.put(configEntry.getId(), createTsl2591LuminositySensor(configEntry.getPortAdress()));
                break;
            case ADC:
                adcConnectedSensorPorts.put(configEntry.getId(), createAdcConnectedSensor(configEntry.getPortAdress()));
        }
    }

    protected RelayOutput getRelayPortById(long portId) {
        return relayPorts.get(portId);
    }

    protected DimmerOutput getDimmerPortById(long portId) {
        return dimmerPorts.get(portId);
    }

    protected BinarySensor getBinarySensorPortById(long portId) {
        return binarySensors.get(portId);
    }

    protected Ds18b20TempSensor getTemperatureSensorPortById(long portId) {
        return ds1820SensorPorts.get(portId);
    }

    protected Dht21TempHumiditySensor getTemperatureHumiditySensorPortById(long portId) {
        return dht21SensorPorts.get(portId);
    }

    protected Bme280TempHumidityPressureSensor getBme280TempHumidityPressureSensorPortById(long portId) {
        return bme280SensorPorts.get(portId);
    }

    protected Tsl2591LuminositySensor getTsl2591LuminositySensorPortById(long portId) {
        return tsl2591LuminositySensorPorts.get(portId);
    }

    protected ADCConnectedSensor getADCConnectedSensorPortById(long portId) {
        return adcConnectedSensorPorts.get(portId);
    }

    protected abstract RelayOutput createRelayOutput(int addr);

    protected abstract DimmerOutput createDimmerOutput(int addr);

    protected abstract BinarySensor createBinarySensor(int addr);

    protected abstract Ds18b20TempSensor createDs1820TemperatureSensor(int addr);

    protected abstract Dht21TempHumiditySensor createDht21TemperatureHumiditySensor(int addr);

    protected abstract Bme280TempHumidityPressureSensor createBme280TempHumidityPressureSensor(int addr);

    protected abstract Tsl2591LuminositySensor createTsl2591LuminositySensor(int addr);

    protected abstract ADCConnectedSensor createAdcConnectedSensor(int addr);

    @Override
    public void onEvent(ControllerEventInfo eventInfo) {
        log.info("Controller #" + eventInfo.getSourceId() + " event: port " + eventInfo.getPort() + " Mode " + eventInfo.getMode());
        PortInfo portInfo = portInfoByAddress.get(eventInfo.getPort());

        if (portInfo != null) {
            switch (portInfo.getPortType()) {
                case BINARY_INPUT:
                    eventPublisher.publishEvent(new BinaryInputInitiatedHwEvent(this, portInfo.getPortId(), portInfo.getPortType(), eventInfo));
                    break;
            }
        }
    }

    protected String getModuleUrl() {
        return moduleUrl;
    }

    private class PortInfo {

        private long portId;
        private MegadPortType portType;

        public PortInfo(long portId, MegadPortType portType) {
            this.portId = portId;
            this.portType = portType;
        }

        public long getPortId() {
            return portId;
        }

        public MegadPortType getPortType() {
            return portType;
        }
    }
}
