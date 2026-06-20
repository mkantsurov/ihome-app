package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.model.constant.BinaryPortStatus;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by maxim on 3/4/23.
 **/
public class CmdGetRelayStatus extends IHomeCommand<BinaryPortStatus> {

    protected CmdGetRelayStatus(long portAddress) {
        super(portAddress);
    }

    @Override
    public BinaryPortStatus dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException {
        return Optional.ofNullable(homePorts.relayPorts().get(getPortAddress())).orElseThrow().getStatus();
    }
}
