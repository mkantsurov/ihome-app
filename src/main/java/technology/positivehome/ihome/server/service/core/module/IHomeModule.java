package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.module.OutputPortStatus;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by maxim on 7/2/19.
 **/
public interface IHomeModule {

    OutputPortStatus getOutputPortStatus() throws URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException;

}
