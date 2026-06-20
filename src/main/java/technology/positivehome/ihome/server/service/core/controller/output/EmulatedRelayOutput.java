package technology.positivehome.ihome.server.service.core.controller.output;

import technology.positivehome.ihome.model.constant.BinaryPortStatus;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;

import java.io.IOException;

/**
 * Created by maxim on 8/18/17.
 **/
public class EmulatedRelayOutput implements RelayOutput {

    private BinaryPortStatus status = BinaryPortStatus.DISABLED;

    public EmulatedRelayOutput() {
    }

    @Override
    public BinaryPortStatus getStatus() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        return status;
    }

    @Override
    public BinaryPortStatus setRelayState(BinaryPortStatus newStatus) throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException {
        status = newStatus;
        return status;
    }

}
