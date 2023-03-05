package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.constant.DimmerPortStatus;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by maxim on 3/4/23.
 **/
public class CmdSetDimmerStatus extends IHomeCommand<DimmerPortStatus> {

    private final DimmerPortStatus status;
    protected CmdSetDimmerStatus(long portAddress, DimmerPortStatus status) {
        super(portAddress);
        this.status = status;
    }

    @Override
    public DimmerPortStatus dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return Optional.ofNullable(homePorts.dimmerPorts().get(getPortAddress())).orElseThrow().setState(status);
    }
}
