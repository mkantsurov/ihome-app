package technology.positivehome.ihome.server.service.core.controller.output;

import technology.positivehome.ihome.model.constant.BinaryPortStatus;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;

import java.io.IOException;

/**
 * Created by maxim on 8/18/17.
 **/
public interface RelayOutput {

    BinaryPortStatus getStatus() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException;

    BinaryPortStatus setRelayState(BinaryPortStatus enable) throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException, InterruptedException;
}
