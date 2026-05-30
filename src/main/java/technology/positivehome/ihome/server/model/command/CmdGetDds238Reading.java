package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Dds238PowerMeterData;

import java.io.IOException;
import java.util.Optional;

public class CmdGetDds238Reading extends IHomeCommand<Dds238PowerMeterData> {

    protected CmdGetDds238Reading(long portAddress) {
        super(portAddress);
    }

    @Override
    public Dds238PowerMeterData dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException {
        return Optional.ofNullable(homePorts.dds238PowerMeterPorts().get(getPortAddress())).orElseThrow().getData();
    }
}
