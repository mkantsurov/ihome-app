package technology.positivehome.ihome.server.service.core.controller.input;


import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;

import java.io.IOException;

/**
 * Created by maxim on 12/29/17.
 **/
public class EmulatedBme280TempHumidityPressureSensor implements Bme280TempHumidityPressureSensor {
    @Override
    public Bme280TempHumidityPressureSensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        return ResultMapper.bme280TempHumidityPressureData("temp:24.00/press:755.26/hum:44.678");
    }
}
