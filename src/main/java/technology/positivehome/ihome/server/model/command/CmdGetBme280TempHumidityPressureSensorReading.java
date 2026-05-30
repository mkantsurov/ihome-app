package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;

import java.io.IOException;
import java.util.Optional;

public class CmdGetBme280TempHumidityPressureSensorReading extends IHomeCommand<Bme280TempHumidityPressureSensorData> {

    protected CmdGetBme280TempHumidityPressureSensorReading(long portAddress) {
        super(portAddress);
    }

    @Override
    public Bme280TempHumidityPressureSensorData dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException {
        return Optional.ofNullable(homePorts.bme280SensorPorts().get(getPortAddress())).orElseThrow().getData();
    }
}
