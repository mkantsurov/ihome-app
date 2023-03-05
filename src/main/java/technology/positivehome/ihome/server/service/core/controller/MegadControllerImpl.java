package technology.positivehome.ihome.server.service.core.controller;

import org.springframework.context.ApplicationEventPublisher;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.controller.ControllerConfigEntry;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.*;
import technology.positivehome.ihome.server.service.core.controller.input.*;
import technology.positivehome.ihome.server.service.core.controller.output.DimmerOutput;
import technology.positivehome.ihome.server.service.core.controller.output.LiveDimmerOutput;
import technology.positivehome.ihome.server.service.core.controller.output.LiveRelayOutput;
import technology.positivehome.ihome.server.service.core.controller.output.RelayOutput;
import technology.positivehome.mgr.processor.megad.controller.input.BinarySensor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by maxim on 7/2/19.
 **/
public class MegadControllerImpl extends AbstractMegadController {

    public MegadControllerImpl(ApplicationEventPublisher eventPublisher, ControllerConfigEntry configEntry) {
        super(eventPublisher, configEntry.ipAddr(), configEntry.portConfig());
    }

    @Override
    protected RelayOutput createRelayOutput(int addr) {
        return new LiveRelayOutput(getModuleUrl(), addr);
    }

    @Override
    protected DimmerOutput createDimmerOutput(int addr) {
        return new LiveDimmerOutput(getModuleUrl(), addr);
    }

    @Override
    protected BinarySensor createBinarySensor(int addr) {
        return new LiveBinarySensor(getModuleUrl(), addr);
    }

    @Override
    protected Ds18b20TempSensor createDs1820TemperatureSensor(int addr) {
        return new LiveDs18b20TempSensor(getModuleUrl(), addr);
    }

    @Override
    protected Dht21TempHumiditySensor createDht21TemperatureHumiditySensor(int addr) {
        return new LiveDht21TempHumiditySensor(getModuleUrl(), addr);
    }

    @Override
    protected Bme280TempHumidityPressureSensor createBme280TempHumidityPressureSensor(int addr) {
        return new LiveBme280TempHumidityPressureSensor(getModuleUrl(), addr);
    }

    @Override
    protected Tsl2591LuminositySensor createTsl2591LuminositySensor(int addr) {
        return new LiveTsl2591LuminositySensor(getModuleUrl(), addr);
    }

    @Override
    protected ADCConnectedSensor createAdcConnectedSensor(int addr) {
         return new LiveADCConnectedSensor(getModuleUrl(), addr);
    }

}
