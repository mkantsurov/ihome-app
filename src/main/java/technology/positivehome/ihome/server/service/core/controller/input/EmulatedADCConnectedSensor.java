package technology.positivehome.ihome.server.service.core.controller.input;

import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.ADCConnectedSensorData;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by maxim on 8/2/21.
 **/
public class EmulatedADCConnectedSensor implements ADCConnectedSensor {
    @Override
    public ADCConnectedSensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        return ResultMapper.adcSensorData(LocalDateTime.now().getSecond() + "");
    }
}
