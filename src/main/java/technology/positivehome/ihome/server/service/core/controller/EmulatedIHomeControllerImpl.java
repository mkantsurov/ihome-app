package technology.positivehome.ihome.server.service.core.controller;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.controller.ControllerConfigEntry;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Tsl2591LuminositySensorData;
import technology.positivehome.ihome.server.service.core.controller.input.*;
import technology.positivehome.ihome.server.service.core.controller.output.DimmerOutput;
import technology.positivehome.ihome.server.service.core.controller.output.EmulatedDimmerOutput;
import technology.positivehome.ihome.server.service.core.controller.output.EmulatedRelayOutput;
import technology.positivehome.ihome.server.service.core.controller.output.RelayOutput;
import technology.positivehome.ihome.server.service.util.IHomeEventBus;
import technology.positivehome.mgr.processor.megad.controller.input.BinarySensor;

import java.io.IOException;

/**
 * Created by maxim on 7/2/19.
 **/
public class EmulatedIHomeControllerImpl extends AbstractIHomeController {

    public EmulatedIHomeControllerImpl(IHomeEventBus eventBus, ControllerConfigEntry configEntry) {
        super(eventBus, configEntry.getIpAddress(), configEntry.getPortConfig());
    }

    @Override
    public synchronized boolean getRelayStatus(long portId) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException {
        return getRelayPortById(portId).getStatus();
    }

    @Override
    public synchronized boolean setRelayState(long portId, boolean enable) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return getRelayPortById(portId).setRelayState(enable);
    }

    @Override
    public int getDimmerStatus(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException {
        return getDimmerPortById(portId).getStatus();
    }

    @Override
    public int setDimmerState(long portId, int value) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, InterruptedException, IOException {
        return getDimmerPortById(portId).setState(value);
    }

    @Override
    public synchronized BinaryPortStatus getBinaryPortStatus(long portId) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException {
        return BinaryPortStatus.ENABLED;
    }

    @Override
    public synchronized Ds18b20TempSensorData getTemperatureSensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException {
        return getTemperatureSensorPortById(portId).getData();
    }

    @Override
    public Dht21TempHumiditySensorData getTemperatureHumiditySensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException {
        return getTemperatureHumiditySensorPortById(portId).getData();
    }

    @Override
    public Bme280TempHumidityPressureSensorData getBme280TempHumidityPressureSensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException {
        return getBme280TempHumidityPressureSensorPortById(portId).getData();
    }

    @Override
    public Tsl2591LuminositySensorData getTsl2591LuminositySensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException {
        return getTsl2591LuminositySensorPortById(portId).getData();
    }

    @Override
    protected RelayOutput createRelayOutput(int addr) {
        return new EmulatedRelayOutput();
    }

    @Override
    protected DimmerOutput createDimmerOutput(int addr) {
        return new EmulatedDimmerOutput();
    }

    @Override
    protected BinarySensor createBinarySensor(int addr) {
        return new EmulatedBinarySensor(addr);
    }

    @Override
    protected Ds18b20TempSensor createDs1820TemperatureSensor(int addr) {
        return new EmulatedDs18b20TempSensor();
    }

    @Override
    protected Dht21TempHumiditySensor createDht21TemperatureHumiditySensor(int addr) {
        return new EmulatedDht21TempHumiditySensor();
    }

    @Override
    protected Bme280TempHumidityPressureSensor createBme280TempHumidityPressureSensor(int addr) {
        return new EmulatedBme280TempHumidityPressureSensor();
    }

    @Override
    protected Tsl2591LuminositySensor createTsl2591LuminositySensor(int addr) {
        return new EmulatedTsl2591LuminositySensor();
    }
}
