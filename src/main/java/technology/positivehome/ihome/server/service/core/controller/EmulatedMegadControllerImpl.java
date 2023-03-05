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
import technology.positivehome.ihome.server.service.core.controller.output.EmulatedDimmerOutput;
import technology.positivehome.ihome.server.service.core.controller.output.EmulatedRelayOutput;
import technology.positivehome.ihome.server.service.core.controller.output.RelayOutput;
import technology.positivehome.mgr.processor.megad.controller.input.BinarySensor;

import java.io.IOException;

/**
 * Created by maxim on 7/2/19.
 **/
public class EmulatedMegadControllerImpl extends AbstractMegadController {

    public EmulatedMegadControllerImpl(ApplicationEventPublisher eventPublisher, ControllerConfigEntry configEntry) {
        super(eventPublisher, configEntry.ipAddr(), configEntry.portConfig());
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

    @Override
    protected ADCConnectedSensor createAdcConnectedSensor(int addr) {
        return new EmulatedADCConnectedSensor();
    }
}
