package technology.positivehome.ihome.server.service.core.controller.input;

import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.sensor.Ds18b20TempSensorData;

import java.io.IOException;

/**
 * Created by maxim on 8/7/17.
 **/
public interface Ds18b20TempSensor {

    Ds18b20TempSensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException;

}
