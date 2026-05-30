package technology.positivehome.ihome.server.processor;

import technology.positivehome.ihome.domain.runtime.*;
import technology.positivehome.ihome.domain.shared.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class DataMapper {

    public static OutDoorTempStatInfo from(OutDoorTempStat res) {
        OutDoorTempStatInfo result = new OutDoorTempStatInfo();
        res.temperature().forEach(chartPoint -> {
            result.getTemperature().add(from(chartPoint));
        });
        return result;
    }

    public static PowerVoltageStatInfo from(PowerVoltageStat res) {
        PowerVoltageStatInfo result = PowerVoltageStatInfo.newInstance();

        res.extVoltage().forEach(chartPoint -> {
            result.extVoltage().add(from(chartPoint));
        });
        res.intVoltage().forEach(chartPoint -> {
            result.intVoltage().add(from(chartPoint));
        });
        res.intBckVoltage().forEach(chartPoint -> {
            result.intBckVoltage().add(from(chartPoint));
        });
        return result;
    }

    public static PowerConsumptionStatInfo from(PowerConsumptionStat res) {
        PowerConsumptionStatInfo result = PowerConsumptionStatInfo.newInstance();
        res.extConsumption().forEach(chartPoint -> {
            result.extConsumption().add(from(chartPoint));
        });
        res.intConsumption().forEach(chartPoint -> {
            result.intConsumption().add(from(chartPoint));
        });
        res.intBckConsumption().forEach(chartPoint -> {
            result.intBckConsumption().add(from(chartPoint));
        });
        return result;
    }

    public static PressureStatInfo from(PressureStat res) {
        PressureStatInfo result = new PressureStatInfo(new ArrayList<>());
        res.pressure().forEach(chartPoint -> {
            result.pressure().add(from(chartPoint));
        });
        return result;
    }

    public static TempStatInfo from(TempStat res) {
        TempStatInfo result = TempStatInfo.newInstance();
        res.garage().forEach(chartPoint -> result.garage().add(from(chartPoint)));
        res.indoorSf().forEach(chartPoint -> result.indoorSf().add(from(chartPoint)));
        res.indoorGf().forEach(chartPoint -> result.indoorGf().add(from(chartPoint)));
        res.outdoor().forEach(chartPoint -> result.outdoor().add(from(chartPoint)));
        return result;
    }

    public static LuminosityStatInfo from(LuminosityStat res) {
        LuminosityStatInfo result = new LuminosityStatInfo(new ArrayList<>());
        res.luminosity().forEach(chartPoint -> {
            result.luminosity().add(from(chartPoint));
        });
        return result;
    }

    public static BoilerTempStatInfo from(BoilerTempStat res) {
        BoilerTempStatInfo result = new BoilerTempStatInfo(new ArrayList<>());
        res.temperature().forEach(chartPoint -> result.temperature().add(from(chartPoint)));
        return result;
    }

    public static SystemStatInfo from(SystemStat res) {
        SystemStatInfo result = SystemStatInfo.newInstance();
        res.heapMax().forEach(chartPoint -> result.heapMax().add(from(chartPoint)));
        res.heapUsage().forEach(chartPoint -> result.heapUsage().add(from(chartPoint)));
        return result;
    }

    public static LaStatInfo from(LaStat res) {
        LaStatInfo result = new LaStatInfo(new ArrayList<>());
        res.la().forEach(chartPoint -> result.la().add(from(chartPoint)));
        return result;
    }

    private static ChartPointInfo from(ChartPoint chartPoint) {
        return new ChartPointInfo(
                ZonedDateTime.of(chartPoint.dt(), ZoneId.of("UTC")),
                chartPoint.value()
        );
    }

}
