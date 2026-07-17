package technology.positivehome.ihome.server.service.core.controller.input;


import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.sensor.Dht21TempHumiditySensorData;

import java.io.IOException;

/**
 * Created by maxim on 9/12/17.
 **/
public interface Dht21TempHumiditySensor {
    Dht21TempHumiditySensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException;
}
