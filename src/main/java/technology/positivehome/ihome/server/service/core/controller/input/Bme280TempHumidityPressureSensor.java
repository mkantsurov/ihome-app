package technology.positivehome.ihome.server.service.core.controller.input;

import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.sensor.Bme280TempHumidityPressureSensorData;

import java.io.IOException;

public interface Bme280TempHumidityPressureSensor {
    Bme280TempHumidityPressureSensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException;
}
