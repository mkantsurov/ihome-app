package technology.positivehome.ihome.server.service.core.controller.output;


import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;

import java.io.IOException;

/**
 * Created by maxim on 12/3/17.
 **/
public interface DimmerOutput {

    int getStatus() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException;

    int setState(int value) throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException, InterruptedException;

}
