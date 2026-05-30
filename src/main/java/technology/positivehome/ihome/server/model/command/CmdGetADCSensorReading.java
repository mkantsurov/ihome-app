package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.ADCConnectedSensorData;

import java.io.IOException;
import java.util.Optional;

public class CmdGetADCSensorReading extends IHomeCommand<ADCConnectedSensorData> {

    protected CmdGetADCSensorReading(long portAddress) {
        super(portAddress);
    }

    @Override
    public ADCConnectedSensorData dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException {
        return Optional.ofNullable(homePorts.adcConnectedSensorPorts().get(getPortAddress())).orElseThrow().getData();
    }

}
