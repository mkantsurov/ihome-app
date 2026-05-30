package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;

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
