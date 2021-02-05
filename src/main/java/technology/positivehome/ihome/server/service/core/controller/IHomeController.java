package technology.positivehome.ihome.server.service.core.controller;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.controller.ControllerPortConfigEntry;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Tsl2591LuminositySensorData;

import java.io.IOException;

/**
 * Created by maxim on 6/30/19.
 **/
public interface IHomeController {

    void addPort(ControllerPortConfigEntry configEntry);

    boolean getRelayStatus(long portId) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException;

    boolean setRelayState(long portId, boolean enable) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException;

    int getDimmerStatus(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;

    int setDimmerState(long portId, int value) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, InterruptedException, IOException;

    BinaryPortStatus getBinaryPortStatus(long portId) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException;

    Ds18b20TempSensorData getTemperatureSensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;

    Dht21TempHumiditySensorData getTemperatureHumiditySensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;

    Bme280TempHumidityPressureSensorData getBme280TempHumidityPressureSensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;

    Tsl2591LuminositySensorData getTsl2591LuminositySensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;

    void onEvent(ControllerEventInfo port);
}
