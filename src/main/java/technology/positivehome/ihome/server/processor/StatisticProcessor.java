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

    private Map<LocalDateTime, SystemSummaryInfo> statCache = new ConcurrentHashMap<>();

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

    @Scheduled(cron = "0 */15 * * * *")
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
        TempStat res = new TempStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.INDOOR_TEMP, res.getIndoor(), entry.getKey(), entry.getValue());
            addChartPoint(ChartType.INDOOR_GF_TEMP, res.getIndoorGf(), entry.getKey(), entry.getValue());
            addChartPoint(ChartType.OUTDOOR_TEMP, res.getOutdoor(), entry.getKey(), entry.getValue());
            addChartPoint(ChartType.GARAGE_TEMP, res.getGarage(), entry.getKey(), entry.getValue());
        }
        if (res.getOutdoor().isEmpty()) {
            SystemSummaryInfo ssi = SystemSummaryInfo.builder(System.currentTimeMillis()).build();
            addChartPoint(ChartType.INDOOR_TEMP, res.getIndoor(), LocalDateTime.now(), ssi);
            addChartPoint(ChartType.INDOOR_GF_TEMP, res.getIndoorGf(), LocalDateTime.now(), ssi);
            addChartPoint(ChartType.OUTDOOR_TEMP, res.getOutdoor(), LocalDateTime.now(), ssi);
            addChartPoint(ChartType.GARAGE_TEMP, res.getGarage(), LocalDateTime.now(), ssi);
        }
        res.getIndoor().sort(Comparator.comparing(ChartPoint::getDt));
        res.getIndoorGf().sort(Comparator.comparing(ChartPoint::getDt));
        res.getOutdoor().sort(Comparator.comparing(ChartPoint::getDt));
        res.getGarage().sort(Comparator.comparing(ChartPoint::getDt));
        return DataMapper.from(res);
    }

    public PressureStatInfo getPressureStat() {
        PressureStat res = new PressureStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.PRESSURE, res.getPressure(), entry.getKey(), entry.getValue());
        }
        if (res.getPressure().isEmpty()) {
            addChartPoint(ChartType.PRESSURE, res.getPressure(), LocalDateTime.now(), SystemSummaryInfo.builder(System.currentTimeMillis()).build());
        }
        res.getPressure().sort(Comparator.comparing(ChartPoint::getDt));
        return DataMapper.from(res);
    }

    private void addChartPoint(ChartType type, List<ChartPoint> data, LocalDateTime ts, SystemSummaryInfo systemSummaryInfo) {
        switch (type) {
            case INDOOR_TEMP:
                data.add(new ChartPoint(ts, systemSummaryInfo.sfTemperature()));
                break;
            case INDOOR_GF_TEMP:
                data.add(new ChartPoint(ts, systemSummaryInfo.gfTemperature()));
                break;
            case OUTDOOR_TEMP:
                data.add(new ChartPoint(ts, systemSummaryInfo.outDoorTemperature()));
                break;
            case GARAGE_TEMP:
                data.add(new ChartPoint(ts, systemSummaryInfo.garageTemperature()));
                break;
            case PRESSURE:
                data.add(new ChartPoint(ts, systemSummaryInfo.pressure()));
                break;
            case BOILER_TEMP:
                data.add(new ChartPoint(ts, systemSummaryInfo.boilerTemperature()));
                break;
            case LUMINOSITY:
                data.add(new ChartPoint(ts, systemSummaryInfo.luminosity()));
                break;
            case SYSTEM_LA:
                data.add(new ChartPoint(ts, systemSummaryInfo.loadAvg()));
                break;
            case SYSTEM_HEAP_MAX:
                data.add(new ChartPoint(ts, systemSummaryInfo.heapMax()));
                break;
            case SYSTEM_HEAP_USAGE:
                data.add(new ChartPoint(ts, systemSummaryInfo.heapUsage()));
                break;
            case EXT_POWER_VOLTAGE:
                data.add(new ChartPoint(ts, systemSummaryInfo.extPwrVoltage()));
                break;
            case INT_POWER_VOLTAGE:
                data.add(new ChartPoint(ts, systemSummaryInfo.intPwrVoltage()));
                break;
            case EXT_POWER_CONSUMPTION:
                data.add(new ChartPoint(ts, systemSummaryInfo.extPwrConsumption()));
                break;
            case INT_POWER_CONSUMPTION:
                data.add(new ChartPoint(ts, systemSummaryInfo.intPwrConsumption()));
                break;
        }
    }

    public BoilerTempStatInfo getBoilerTempStat() {
        BoilerTempStat res = new BoilerTempStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.BOILER_TEMP, res.getTemperature(), entry.getKey(), entry.getValue());
        }
        if (res.getTemperature().isEmpty()) {
            addChartPoint(ChartType.BOILER_TEMP, res.getTemperature(), LocalDateTime.now(), SystemSummaryInfo.builder(System.currentTimeMillis()).build());
        }
        res.getTemperature().sort(Comparator.comparing(ChartPoint::getDt));
        return DataMapper.from(res);
    }

    public LuminosityStatInfo getLuminosityStat() {
        LuminosityStat res = new LuminosityStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.LUMINOSITY, res.getLuminosity(), entry.getKey(), entry.getValue());
        }
        if (res.getLuminosity().isEmpty()) {
            addChartPoint(ChartType.LUMINOSITY, res.getLuminosity(), LocalDateTime.now(), SystemSummaryInfo.builder(System.currentTimeMillis()).build());
        }
        res.getLuminosity().sort(Comparator.comparing(ChartPoint::getDt));
        return DataMapper.from(res);
    }

    public SystemStatInfo getSystemStat() {
        SystemStat res = new SystemStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.SYSTEM_HEAP_MAX, res.getHeapMax(), entry.getKey(), entry.getValue());
            addChartPoint(ChartType.SYSTEM_HEAP_USAGE, res.getHeapUsage(), entry.getKey(), entry.getValue());
        }
        if (res.getHeapMax().isEmpty()) {
            addChartPoint(ChartType.SYSTEM_HEAP_MAX, res.getHeapMax(), LocalDateTime.now(), SystemSummaryInfo.builder(System.currentTimeMillis()).build());
            addChartPoint(ChartType.SYSTEM_HEAP_USAGE, res.getHeapUsage(), LocalDateTime.now(), SystemSummaryInfo.builder(System.currentTimeMillis()).build());
        }
        res.getHeapMax().sort(Comparator.comparing(ChartPoint::getDt));
        res.getHeapUsage().sort(Comparator.comparing(ChartPoint::getDt));
        return DataMapper.from(res);
    }

    public LaStatInfo getLaStat() {
        LaStat res = new LaStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.SYSTEM_LA, res.getLa(), entry.getKey(), entry.getValue());
        }

        if (res.getLa().isEmpty()) {
            addChartPoint(ChartType.SYSTEM_LA, res.getLa(), LocalDateTime.now(), SystemSummaryInfo.builder(System.currentTimeMillis()).build());
        }
        res.getLa().sort(Comparator.comparing(ChartPoint::getDt));
        return DataMapper.from(res);
    }

    public OutDoorTempStatInfo getTemperatureStat() {
        OutDoorTempStat res = new OutDoorTempStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.OUTDOOR_TEMP, res.getTemperature(), entry.getKey(), entry.getValue());
        }
        res.getTemperature().sort(Comparator.comparing(ChartPoint::getDt));
        return DataMapper.from(res);
    }
    public PowerVoltageExtStatInfo getPowerVoltageExtStat() {
        PowerVoltageStat res = new PowerVoltageStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.EXT_POWER_VOLTAGE, res.getExtVoltage(), entry.getKey(), entry.getValue());
            addChartPoint(ChartType.INT_POWER_VOLTAGE, res.getIntVoltage(), entry.getKey(), entry.getValue());
        }
        res.getExtVoltage().sort(Comparator.comparing(ChartPoint::getDt));
        res.getIntVoltage().sort(Comparator.comparing(ChartPoint::getDt));
        return new PowerVoltageExtStatInfo(DataMapper.from(res).getExtVoltage());
    }
    public PowerVoltageStatInfo getPowerVoltageStat() {
        PowerVoltageStat res = new PowerVoltageStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.EXT_POWER_VOLTAGE, res.getExtVoltage(), entry.getKey(), entry.getValue());
            addChartPoint(ChartType.INT_POWER_VOLTAGE, res.getIntVoltage(), entry.getKey(), entry.getValue());
        }
        res.getExtVoltage().sort(Comparator.comparing(ChartPoint::getDt));
        res.getIntVoltage().sort(Comparator.comparing(ChartPoint::getDt));
        return DataMapper.from(res);
    }

    public PowerConsumptionStatInfo getPowerConsumptionStat() {
        PowerConsumptionStat res = new PowerConsumptionStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        List<ChartPoint> extConsumption = new ArrayList<>();
        List<ChartPoint> intConsumption = new ArrayList<>();

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            extConsumption.add(new ChartPoint(entry.getKey(), entry.getValue().extPwrConsumption()));
            intConsumption.add(new ChartPoint(entry.getKey(), entry.getValue().intPwrConsumption()));
        }
        extConsumption.sort(Comparator.comparing(ChartPoint::getDt));
        intConsumption.sort(Comparator.comparing(ChartPoint::getDt));

        res.setExtConsumption(normalize(extConsumption));
        res.setIntConsumption(normalize(intConsumption));
        return DataMapper.from(res);
    }

    private List<ChartPoint> normalize(List<ChartPoint> data) {
        data.sort(Comparator.comparing(ChartPoint::getDt));
        List<ChartPoint> res = new ArrayList<>();

        int curValue = 0;
        int startPoint;

        for (startPoint = 0; startPoint < data.size() && curValue <= 0; startPoint ++) {
            curValue = data.get(startPoint).getValue();
        }

        for (int i = startPoint + 1; i < data.size(); i++) {
            int diff = data.get(i).getValue() - curValue;
            if (diff >= 0 && diff < 200) {
                res.add(new ChartPoint(data.get(i).getDt(), data.get(i).getValue() - curValue));
                curValue = data.get(i).getValue();
            } else {
                log.warn("Invalid diff value: " + diff + " Prev Value: " + curValue + " Current value: " + data.get(i).getValue());
            }
        }
        return res;
    }

    private enum ChartType {
        INDOOR_TEMP,
        INDOOR_GF_TEMP,
        OUTDOOR_TEMP,
        GARAGE_TEMP,
        PRESSURE,
        BOILER_TEMP,
        LUMINOSITY,
        SYSTEM_LA,
        SYSTEM_HEAP_MAX,
        SYSTEM_HEAP_USAGE,
        EXT_POWER_VOLTAGE,
        EXT_POWER_CURRENT,
        EXT_POWER_FREQUENCY,
        EXT_POWER_CONSUMPTION,
        INT_POWER_VOLTAGE,
        INT_POWER_CURRENT,
        INT_POWER_FREQUENCY,
        INT_POWER_CONSUMPTION

    }
}
