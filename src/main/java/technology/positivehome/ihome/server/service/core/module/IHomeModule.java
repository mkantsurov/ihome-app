package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Tsl2591LuminositySensorData;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by maxim on 7/2/19.
 **/
public interface IHomeModule {

    OutputPortStatus getOutputPortStatus() throws URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException;

    BinaryPortStatus getBinarySensorData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;

    Ds18b20TempSensorData getTemperatureSensorReading(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;

    Dht21TempHumiditySensorData getTemperatureHumiditySensorData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;

    Bme280TempHumidityPressureSensorData getBme280TempHumidityPressureSensorReading(long elementId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;

    Tsl2591LuminositySensorData getTsl2591LuminositySensorReading(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;
}
