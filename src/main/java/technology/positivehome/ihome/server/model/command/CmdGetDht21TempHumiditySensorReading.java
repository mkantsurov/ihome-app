package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.sensor.Dht21TempHumiditySensorData;

import java.io.IOException;
import java.util.Optional;

public class CmdGetDht21TempHumiditySensorReading extends IHomeCommand<Dht21TempHumiditySensorData> {

    protected CmdGetDht21TempHumiditySensorReading(long portAddress) {
        super(portAddress);
    }

    @Override
    public Dht21TempHumiditySensorData dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException {
        return Optional.ofNullable(homePorts.dht21SensorPorts().get(getPortAddress())).orElseThrow().getData();
    }
}
