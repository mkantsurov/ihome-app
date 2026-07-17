package technology.positivehome.ihome.server.service.core.controller.input;

import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.sensor.Dds238PowerMeterData;

import java.io.IOException;

public interface Dds238PowerMeter {
    Dds238PowerMeterData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException, InterruptedException;
}
