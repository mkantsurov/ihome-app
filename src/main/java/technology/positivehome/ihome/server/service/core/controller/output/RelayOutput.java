package technology.positivehome.ihome.server.service.core.controller.output;

import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;

import java.io.IOException;

/**
 * Created by maxim on 8/18/17.
 **/
public interface RelayOutput {

    boolean getStatus() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException;

    boolean setRelayState(boolean enable) throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException, InterruptedException;
}
