package technology.positivehome.ihome.server.service.core.controller.input;

import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Tsl2591LuminositySensorData;

import java.io.IOException;

/**
 * Created by maxim on 2/23/20.
 **/
public class EmulatedTsl2591LuminositySensor implements Tsl2591LuminositySensor {
    @Override
    public Tsl2591LuminositySensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        return ResultMapper.tsl2591LuminositySensorData("1443.77");
    }
}
