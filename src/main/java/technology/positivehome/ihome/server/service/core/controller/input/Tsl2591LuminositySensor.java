package technology.positivehome.ihome.server.service.core.controller.input;

import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.sensor.Tsl2591LuminositySensorData;

import java.io.IOException;

/**
 * Created by maxim on 2/23/20.
 **/
public interface Tsl2591LuminositySensor {
    Tsl2591LuminositySensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException;
}
