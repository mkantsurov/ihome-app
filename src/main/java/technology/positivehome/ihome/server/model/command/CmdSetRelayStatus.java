package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by maxim on 3/4/23.
 **/
public class CmdSetRelayStatus extends IHomeCommand<BinaryPortStatus> {

    private final BinaryPortStatus status;
    protected CmdSetRelayStatus(long portAddress, BinaryPortStatus status) {
        super(portAddress);
        this.status = status;
    }

    public BinaryPortStatus getStatus() {
        return status;
    }

    @Override
    public BinaryPortStatus dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return Optional.ofNullable(homePorts.relayPorts().get(getPortAddress())).orElseThrow().setRelayState(status);
    }
}
