package technology.positivehome.ihome.server.service.core.controller.output;

import technology.positivehome.ihome.domain.constant.DimmerPortStatus;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;

import java.io.IOException;

/**
 * Created by maxim on 12/3/17.
 **/
public class EmulatedDimmerOutput implements DimmerOutput {

    DimmerPortStatus currentState = DimmerPortStatus.OFF;

    @Override
    public DimmerPortStatus getStatus() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        return currentState;
    }

    @Override
    public DimmerPortStatus setState(DimmerPortStatus value) throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException, InterruptedException {
        currentState = value;
        return currentState;
    }
}
