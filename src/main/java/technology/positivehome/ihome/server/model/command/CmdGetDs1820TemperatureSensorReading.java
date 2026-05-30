package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;

import java.io.IOException;
import java.util.Optional;

public class CmdGetDs1820TemperatureSensorReading extends IHomeCommand<Ds18b20TempSensorData> {

    protected CmdGetDs1820TemperatureSensorReading(long portAddress) {
        super(portAddress);
    }

    @Override
    public Ds18b20TempSensorData dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException {
        return Optional.ofNullable(homePorts.ds1820SensorPorts().get(getPortAddress())).orElseThrow().getData();
    }
}
