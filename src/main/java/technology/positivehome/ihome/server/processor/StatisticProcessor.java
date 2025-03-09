package technology.positivehome.ihome.server.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.runtime.*;
import technology.positivehome.ihome.domain.runtime.event.MeasurementLogEntity;
import technology.positivehome.ihome.domain.shared.*;
import technology.positivehome.ihome.server.persistence.MeasurementsLogRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by maxim on 8/17/19.
 **/
@Component
@EnableScheduling
public class StatisticProcessor implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(SystemProcessor.class);
    private final SystemProcessor systemProcessor;
    private final MeasurementsLogRepository measurementsLogRepository;

    private final Map<LocalDateTime, SystemSummaryInfo> statCache = new ConcurrentHashMap<>();

    public StatisticProcessor(SystemProcessor systemProcessor, MeasurementsLogRepository measurementsLogRepository) {
        this.systemProcessor = systemProcessor;
        this.measurementsLogRepository = measurementsLogRepository;
    }

    @Override
    public void afterPropertiesSet() {

        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1);
        LocalDateTime startTime = endTime.minusHours(48);

        List<MeasurementLogEntity> res = measurementsLogRepository.readDataForPeriod(startTime, endTime);
        for (MeasurementLogEntity entry : res) {
            statCache.put(entry.created(), SystemSummaryInfo.of(entry));
        }
    }

    @Scheduled(cron = "0 */5 * * * *")
    protected void collectStat() {
        try {
            SystemSummaryInfo si = systemProcessor.getSummaryInfo();
            Iterator<Map.Entry<LocalDateTime, SystemSummaryInfo>> it = statCache.entrySet().iterator();
            LocalDateTime dt2DaysBefore = LocalDateTime.now().minusDays(2);
            while (it.hasNext()) {
                if (it.next().getKey().isBefore(dt2DaysBefore)) {
                    it.remove();
                }
            }
            statCache.put(LocalDateTime.now(), si);
            measurementsLogRepository.writeLogEntry(MeasurementLogEntity.of(si));
        } catch (Exception ex) {
            log.error("Problem collecting system stat", ex);
        }
    }

    public TempStatInfo getTempStat() {
        TempStat.Builder res = TempStat.builder();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            res.withIndoorSfTempChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().sfTemperature()))
                    .withIndoorGfTempChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().gfTemperature()))
                    .withOutDoorTempChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().outDoorTemperature()))
                    .withGarageTempChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().garageTemperature()));
        }
        return DataMapper.from(res.build());
    }

    public PressureStatInfo getPressureStat() {
        ChartDataBuilder<PressureStat> res = PressureStat.builder();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            res.withChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().pressure()));
        }
        return DataMapper.from(res.build(PressureStat::new));
    }

    public BoilerTempStatInfo getBoilerTempStat() {
        ChartDataBuilder<BoilerTempStat> res = BoilerTempStat.builder();

        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            res.withChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().boilerTemperature()));
        }
        return DataMapper.from(res.build(BoilerTempStat::new));
    }

    public LuminosityStatInfo getLuminosityStat() {
        ChartDataBuilder<LuminosityStat> res = LuminosityStat.builder();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            res.withChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().luminosity()));
        }
        return DataMapper.from(res.build(LuminosityStat::new));
    }

    public SystemStatInfo getSystemStat() {
        SystemStat.Builder res = SystemStat.builder();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            res.withChartPoint(entry.getKey(), entry.getValue().heapMax(), entry.getValue().heapUsage());
        }
        return DataMapper.from(res.build());
    }

    public LaStatInfo getLaStat() {
        ChartDataBuilder<LaStat> res = LaStat.builder();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            res.withChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().loadAvg()));
        }
        return DataMapper.from(res.build(LaStat::new));
    }

    public OutDoorTempStatInfo getTemperatureStat() {
        ChartDataBuilder<OutDoorTempStat> res = OutDoorTempStat.builder();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            res.withChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().outDoorTemperature()));
        }
        return DataMapper.from(res.build(OutDoorTempStat::new));
    }
    public PowerVoltageExtStatInfo getPowerVoltageExtStat() {
        PowerVoltageStat.Builder res = PowerVoltageStat.builder();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            res.withExtVoltageChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().extPwrVoltage()))
                    .withIntVoltageChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().intPwrVoltage()))
                    .withIntBckVoltageChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().intBckPwrVoltage()));
        }
        return new PowerVoltageExtStatInfo(DataMapper.from(res.build()).extVoltage());
    }
    public PowerVoltageStatInfo getPowerVoltageStat() {
        PowerVoltageStat.Builder res = PowerVoltageStat.builder();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            res.withExtVoltageChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().extPwrVoltage()))
                    .withIntVoltageChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().intPwrVoltage()))
                    .withIntBckVoltageChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().intBckPwrVoltage()));
        }
        return DataMapper.from(res.build());
    }

    public PowerConsumptionStatInfo getPowerConsumptionStat() {
        PowerConsumptionStatBuilder res = PowerConsumptionStatBuilder.getInstance();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            res.withExtConsumptionChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().extPwrConsumption()))
                    .withIntConsumptionChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().intPwrConsumption()))
                    .withIntBckConsumptionChartPoint(ChartPoint.of(entry.getKey(), entry.getValue().intBckPwrConsumption()));
        }
        return DataMapper.from(res.build());
    }

}
