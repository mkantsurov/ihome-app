package technology.positivehome.ihome.server.processor;

import technology.positivehome.ihome.domain.runtime.*;
import technology.positivehome.ihome.domain.shared.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DataMapper {

    public static OutDoorTempStatInfo from(OutDoorTempStat res) {
        OutDoorTempStatInfo result = new OutDoorTempStatInfo();
        res.getTemperature().forEach(chartPoint -> {
            result.getTemperature().add(from(chartPoint));
        });
        return result;
    }

    public static PowerStatInfo from(PowerStat res) {
        PowerStatInfo result = new PowerStatInfo();
        res.getPower().forEach(chartPoint -> {
            result.getPower().add(from(chartPoint));
        });
        return result;
    }

    public static PressureStatInfo from(PressureStat res) {
        PressureStatInfo result = new PressureStatInfo();
        res.getPressure().forEach(chartPoint -> {
            result.getPressure().add(from(chartPoint));
        });
        return result;
    }

    public static TempStatInfo from(TempStat res) {
        TempStatInfo result = new TempStatInfo();
        res.getGarage().forEach(chartPoint -> result.getGarage().add(from(chartPoint)));
        res.getIndoor().forEach(chartPoint -> result.getIndoor().add(from(chartPoint)));
        res.getIndoorGf().forEach(chartPoint -> result.getIndoorGf().add(from(chartPoint)));
        res.getOutdoor().forEach(chartPoint -> result.getOutdoor().add(from(chartPoint)));
        return result;
    }

    public static LuminosityStatInfo from(LuminosityStat res) {
        LuminosityStatInfo result = new LuminosityStatInfo();
        res.getLuminosity().forEach(chartPoint -> {
            result.getLuminosity().add(from(chartPoint));
        });
        return result;
    }

    public static BoilerTempStatInfo from(BoilerTempStat res) {
        BoilerTempStatInfo result = new BoilerTempStatInfo();
        res.getTemperature().forEach(chartPoint -> result.getTemperature().add(from(chartPoint)));
        return result;
    }

    public static SystemStatInfo from(SystemStat res) {
        SystemStatInfo result = new SystemStatInfo();
        res.getHeapMax().forEach(chartPoint -> result.getHeapMax().add(from(chartPoint)));
        res.getHeapUsage().forEach(chartPoint -> result.getHeapUsage().add(from(chartPoint)));
        return result;
    }

    public static LaStatInfo from(LaStat res) {
        LaStatInfo result = new LaStatInfo();
        res.getLa().forEach(chartPoint -> result.getLa().add(from(chartPoint)));
        return result;
    }

    private static ChartPointInfo from(ChartPoint chartPoint) {
        return new ChartPointInfo(
                ZonedDateTime.of(chartPoint.getDt(), ZoneId.of("UTC")),
                chartPoint.getValue()
        );
    }

}
