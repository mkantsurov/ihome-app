package technology.positivehome.ihome.server.model.command;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.constant.DimmerPortStatus;

public class IHomeCommandFactory {
    public static CmdGetADCSensorReading cmdGetADCSensorReading(long port) {
        return new CmdGetADCSensorReading(port);
    }
    public static CmdGetDht21TempHumiditySensorReading cmdGetDht21TempHumiditySensorReading(long port) {
        return new CmdGetDht21TempHumiditySensorReading(port);
    }
    public static CmdGetTsl2591LuminositySensorReading cmdGetTsl2591LuminositySensorReading(long port) {
        return new CmdGetTsl2591LuminositySensorReading(port);
    }
    public static CmdGetBinarySensorReading cmdGetBinarySensorReading(long port) {
        return new CmdGetBinarySensorReading(port);
    }
    public static CmdGetDimmerStatus cmdGetDimmerStatus(long port) {
        return new CmdGetDimmerStatus(port);
    }
    public static CmdSetDimmerStatus cmdSetDimmerStatus(long port, DimmerPortStatus status) {
        return new CmdSetDimmerStatus(port, status);
    }
    public static CmdGetBme280TempHumidityPressureSensorReading cmdGetBme280TempHumidityPressureSensorReading(long port) {
        return new CmdGetBme280TempHumidityPressureSensorReading(port);
    }
    public static CmdGetDs1820TemperatureSensorReading cmdGetDs1820TemperatureSensorReading(long port) {
        return new CmdGetDs1820TemperatureSensorReading(port);
    }
    public static CmdSetRelayStatus cmdSetRelayStatus(long port, BinaryPortStatus status) {
        return new CmdSetRelayStatus(port, status);
    }
    public static CmdGetDds238Reading cmdGetDds238Reading(long port) {
        return new CmdGetDds238Reading(port);
    }
    public static CmdGetRelayStatus cmdGetRelayStatus(long port) {
        return new CmdGetRelayStatus(port);
    }

}
