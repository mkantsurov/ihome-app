package technology.positivehome.ihome.server.service.core.controller.input;


import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.sensor.Ds18b20TempSensorData;

import java.io.IOException;

/**
 * Created by maxim on 8/31/17.
 **/
public class EmulatedDs18b20TempSensor implements Ds18b20TempSensor {

    @Override
    public Ds18b20TempSensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        return ResultMapper.ds18b20TempSensorData("temp:24.31");
    }
}
