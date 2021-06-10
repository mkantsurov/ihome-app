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
import technology.positivehome.ihome.server.service.core.controller.output.LiveDimmerOutput;
import technology.positivehome.ihome.server.service.core.controller.output.LiveRelayOutput;
import technology.positivehome.ihome.server.service.core.controller.output.RelayOutput;
import technology.positivehome.ihome.server.service.util.IHomeEventBus;
import technology.positivehome.mgr.processor.megad.controller.input.BinarySensor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by maxim on 7/2/19.
 **/
public class IHomeControllerImpl extends AbstractIHomeController {

    final ReentrantLock lock = new ReentrantLock();

    public IHomeControllerImpl(IHomeEventBus eventBus, ControllerConfigEntry configEntry) {
        super(eventBus, configEntry.getIpAddress(), configEntry.getPortConfig());
    }

    @Override
    public boolean getRelayStatus(long portId) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {
        tryLockOrThrowExcption();
        try {
            return getRelayPortById(portId).getStatus();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean setRelayState(long portId, boolean enable) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {
        tryLockOrThrowExcption();
        try {
            return getRelayPortById(portId).setRelayState(enable);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getDimmerStatus(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        tryLockOrThrowExcption();
        try {
            return getDimmerPortById(portId).getStatus();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int setDimmerState(long portId, int value) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, InterruptedException, IOException {
        tryLockOrThrowExcption();
        try {
            return getDimmerPortById(portId).setState(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public BinaryPortStatus getBinaryPortStatus(long portId) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {
        tryLockOrThrowExcption();
        try {
            return getBinarySensorPortById(portId).getStatus();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Ds18b20TempSensorData getTemperatureSensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        tryLockOrThrowExcption();
        try {
            return getTemperatureSensorPortById(portId).getData();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Dht21TempHumiditySensorData getTemperatureHumiditySensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        tryLockOrThrowExcption();
        try {
            return getTemperatureHumiditySensorPortById(portId).getData();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Bme280TempHumidityPressureSensorData getBme280TempHumidityPressureSensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        tryLockOrThrowExcption();
        try {
            return getBme280TempHumidityPressureSensorPortById(portId).getData();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Tsl2591LuminositySensorData getTsl2591LuminositySensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        tryLockOrThrowExcption();
        try {
            return getTsl2591LuminositySensorPortById(portId).getData();
        } finally {
            lock.unlock();
        }
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

    private void tryLockOrThrowExcption() throws InterruptedException, IOException {
        if (!lock.tryLock(20, TimeUnit.SECONDS)) {
            throw new IOException("Could not acquire lock  in thread controller " + getModuleUrl() + " Thread info: "  + Thread.currentThread());
        }
    }

}
