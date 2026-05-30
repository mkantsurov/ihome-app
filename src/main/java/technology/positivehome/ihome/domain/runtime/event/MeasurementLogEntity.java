package technology.positivehome.ihome.domain.runtime.event;

import technology.positivehome.ihome.domain.runtime.SystemSummaryInfo;

import java.time.LocalDateTime;

/**
 * Created by maxim on 6/25/19.
 **/
public record MeasurementLogEntity(long id, LocalDateTime created, int loadAvg, int heapMax, int heapUsage, int pressure,
                                   int outdoorTemp, int outdoorHumidity, int indoorSfTemp, int indoorSfHumidity,
                                   int indoorGfTemp, int garageTemp, int garageHumidity, int boilerTemperature, int luminosity,
                                   int extPwrVoltage, int extPwrCurrent, int extPwrFrequency, int extPwrConsumption,
                                   int intPwrVoltage, int intPwrCurrent, int intPwrFrequency, int intPwrConsumption,
                                   int intBckPwrVoltage, int intBckPwrCurrent, int intBckPwrFrequency, int intBckPwrConsumption,
                                   int securityMode, int pwSrcConverterMode, int pwSrcDirectMode,
                                   int heatingPumpFFMode, int heatingPumpSFMode) {

    public static MeasurementLogEntity of(SystemSummaryInfo si) {
        return new MeasurementLogEntity(0,
                LocalDateTime.now(),
                si.loadAvg(),
                si.heapMax(),
                si.heapUsage(),
                si.pressure(),
                si.outDoorTemperature(),
                si.outDoorHumidity(),
                si.sfTemperature(),
                si.sfHumidity(),
                si.gfTemperature(),
                si.garageTemperature(),
                si.garageHumidity(),
                si.boilerTemperature(),
                si.luminosity(),
                si.extPwrVoltage(), si.extPwrCurrent(), si.extPwrFrequency(), si.extPwrConsumption(),
                si.intPwrVoltage(), si.intPwrCurrent(), si.intPwrFrequency(), si.intPwrConsumption(),
                si.intBckPwrVoltage(), si.intBckPwrCurrent(), si.intBckPwrFrequency(), si.intBckPwrConsumption(),
                si.securityMode(), si.pwSrcConverterMode(), si.pwSrcDirectMode(), si.heatingPumpFFMode(),
                si.heatingPumpSFMode());
    }
}
