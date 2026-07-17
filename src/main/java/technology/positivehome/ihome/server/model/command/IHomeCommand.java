package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;

import java.io.IOException;

/**
 * Created by maxim on 3/4/23.
 **/
//boolean getRelayStatus(long portId) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException;
//
//    boolean setRelayState(long portId, boolean enable) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException;
//
//    int getDimmerStatus(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;
//
//    int setDimmerState(long portId, int value) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, InterruptedException, IOException;
//
//    BinaryPortStatus getBinaryPortStatus(long portId) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException;
//
//    Ds18b20TempSensorData getTemperatureSensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;
//
//    Dht21TempHumiditySensorData getTemperatureHumiditySensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;
//
//    Bme280TempHumidityPressureSensorData getBme280TempHumidityPressureSensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;
//
//    Tsl2591LuminositySensorData getTsl2591LuminositySensorPortData(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;
//
//    ADCConnectedSensorData getAdcSensorPortData(long portId) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException, InterruptedException;
public abstract class IHomeCommand<R> {
    private final long portAddress;
    protected IHomeCommand(long portAddress) {
        this.portAddress = portAddress;
    }

    public long getPortAddress() {
        return portAddress;
    }

    public abstract R dispatch(IHomePorts homePorts) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException, InterruptedException;
}
