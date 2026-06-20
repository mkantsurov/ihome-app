package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.sensor.Dds238PowerMeterData;

import java.io.IOException;
import java.util.Optional;

public class CmdGetDds238Reading extends IHomeCommand<Dds238PowerMeterData> {

    protected CmdGetDds238Reading(long portAddress) {
        super(portAddress);
    }

    @Override
    public Dds238PowerMeterData dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return Optional.ofNullable(homePorts.dds238PowerMeterPorts().get(getPortAddress())).orElseThrow().getData();
    }
}
