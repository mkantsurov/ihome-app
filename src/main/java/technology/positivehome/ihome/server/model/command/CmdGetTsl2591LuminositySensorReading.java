package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Tsl2591LuminositySensorData;

import java.io.IOException;
import java.util.Optional;

public class CmdGetTsl2591LuminositySensorReading extends IHomeCommand<Tsl2591LuminositySensorData> {

    protected CmdGetTsl2591LuminositySensorReading(long portAddress) {
        super(portAddress);
    }

    @Override
    public Tsl2591LuminositySensorData dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException {
        return Optional.ofNullable(homePorts.tsl2591LuminositySensorPorts().get(getPortAddress())).orElseThrow().getData();
    }
}
