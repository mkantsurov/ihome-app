package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.model.constant.BinaryPortStatus;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;

import java.io.IOException;
import java.util.Optional;

public class CmdGetBinarySensorReading extends IHomeCommand<BinaryPortStatus> {

    protected CmdGetBinarySensorReading(long portAddress) {
        super(portAddress);
    }

    @Override
    public BinaryPortStatus dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException {
        return Optional.ofNullable(homePorts.binarySensors().get(getPortAddress())).orElseThrow().getStatus();
    }
}
