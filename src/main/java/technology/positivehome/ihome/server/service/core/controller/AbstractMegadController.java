package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import technology.positivehome.ihome.domain.runtime.controller.ControllerPortConfigEntry;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.server.model.PortInfo;
import technology.positivehome.ihome.server.model.command.*;
import technology.positivehome.ihome.server.service.core.controller.input.*;
import technology.positivehome.ihome.server.service.core.controller.output.DimmerOutput;
import technology.positivehome.ihome.server.service.core.controller.output.RelayOutput;
import technology.positivehome.mgr.processor.megad.controller.input.BinarySensor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by maxim on 6/30/19.
 **/
public abstract class AbstractMegadController implements IHomeController {
    private static final Log log = LogFactory.getLog(AbstractMegadController.class);
    final ReentrantLock lock = new ReentrantLock();
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

    public AbstractMegadController(ApplicationEventPublisher eventPublisher, String ipAddress, List<ControllerPortConfigEntry> portConfig) {
        this.eventPublisher = eventPublisher;
        this.moduleUrl = "http://" + ipAddress + "/sec/";
        for (ControllerPortConfigEntry configEntry : portConfig) {
            portInfoByAddress.put(configEntry.portAddress(), new PortInfo(configEntry.id(), configEntry.type()));
            addPort(configEntry);
        }
    }

    @Override
    public void addPort(ControllerPortConfigEntry configEntry) {
        switch (configEntry.type()) {
            case RELAY_OUTPUT -> relayPorts.put(configEntry.id(), createRelayOutput(configEntry.portAddress()));
            case DIMMER_OUTPUT -> dimmerPorts.put(configEntry.id(), createDimmerOutput(configEntry.portAddress()));
            case BINARY_INPUT -> binarySensors.put(configEntry.id(), createBinarySensor(configEntry.portAddress()));
            case DS1820_TEMPERATURE_SENSOR ->
                    ds1820SensorPorts.put(configEntry.id(), createDs1820TemperatureSensor(configEntry.portAddress()));
            case DHT21_TEMPERATURE_HUMIDITY_SENSOR ->
                    dht21SensorPorts.put(configEntry.id(), createDht21TemperatureHumiditySensor(configEntry.portAddress()));
            case BME280_TEMP_HUMIDITY_PRESS_SENSOR ->
                    bme280SensorPorts.put(configEntry.id(), createBme280TempHumidityPressureSensor(configEntry.portAddress()));
            case TSL2591_LUMINOSITY_SENSOR ->
                    tsl2591LuminositySensorPorts.put(configEntry.id(), createTsl2591LuminositySensor(configEntry.portAddress()));
            case ADC ->
                    adcConnectedSensorPorts.put(configEntry.id(), createAdcConnectedSensor(configEntry.portAddress()));
        }
    }

    @Override
    public <R> R runCommand(IHomeCommand<R> iHomeCommand) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException, InterruptedException {
        tryLockOrThrowExcption();
        try {
            return iHomeCommand.dispatch(IHomePorts.of(relayPorts, binarySensors, dimmerPorts, ds1820SensorPorts, dht21SensorPorts, bme280SensorPorts, tsl2591LuminositySensorPorts, adcConnectedSensorPorts));
        } finally {
            lock.unlock();
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

    private void tryLockOrThrowExcption() throws InterruptedException, IOException {
        if (!lock.tryLock(20, TimeUnit.SECONDS)) {
            throw new IOException("Could not acquire lock  in thread controller " + getModuleUrl() + " Thread info: "  + Thread.currentThread());
        }
    }
}
