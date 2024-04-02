package technology.positivehome.ihome.domain.runtime;

/**
 * Created by maxim on 6/9/19.
 **/
public record PowerSummaryInfo(int luminosity, int extVoltage, int extCurrent, int extFrequency, int extConsumption,
                               int intVoltage, int intCurrent, int intFrequency, int intConsumption,
                               int intBckVoltage, int intBckCurrent, int intBckFrequency, int intBckConsumption,
                               int securityMode, int pwSrcConverterMode, int pwSrcDirectMode) {
}
