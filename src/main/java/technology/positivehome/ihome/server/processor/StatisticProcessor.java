package technology.positivehome.ihome.server.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.runtime.*;
import technology.positivehome.ihome.domain.runtime.event.MeasurementLogEntry;
import technology.positivehome.ihome.server.persistence.MeasurementsLogRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR, 1);
        Date endTime = calendar.getTime();
        calendar.add(Calendar.HOUR, -48);
        Date startTime = calendar.getTime();
        List<MeasurementLogEntry> res = measurementsLogRepository.readDataForPeriod(startTime, endTime);
        for (MeasurementLogEntry entry : res) {
            LocalDateTime ldt = LocalDateTime.ofInstant(entry.getCreated().toInstant(), ZoneId.systemDefault());
            log.info("Reading measurement history: " + entry.getCreated().toString() + " Converted " + ldt.toString());
            statCache.put(ldt, new SystemSummaryInfo(entry));
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
            MeasurementLogEntry entry = new MeasurementLogEntry(0, new Date(),
                    si.getLoadAvg(),
                    si.getHeapMax(),
                    si.getHeapUsage(),
                    si.getPressure(),
                    si.getOutDoorTemperature(),
                    si.getOutDoorHumidity(),
                    si.getSfTemperature(),
                    si.getSfHumidity(),
                    si.getGfTemperature(),
                    si.getGarageTemperature(),
                    si.getGarageHumidity(),
                    si.getBoilerTemperature(),
                    si.getLuminosity()
            );
            measurementsLogRepository.writeLogEntry(entry);
        } catch (Exception ex) {
            log.error("Problem collecting system stat", ex);
        }
    }

    public TempStat getTempStat() {
        TempStat res = new TempStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.INDOOR_TEMP, res.getIndoor(), entry.getKey(), entry.getValue());
            addChartPoint(ChartType.INDOOR_GF_TEMP, res.getIndoorGf(), entry.getKey(), entry.getValue());
            addChartPoint(ChartType.OUTDOOR_TEMP, res.getOutdoor(), entry.getKey(), entry.getValue());
            addChartPoint(ChartType.GARAGE_TEMP, res.getGarage(), entry.getKey(), entry.getValue());
        }
        if (res.getOutdoor().isEmpty()) {
            SystemSummaryInfo ssi = new SystemSummaryInfo();
            addChartPoint(ChartType.INDOOR_TEMP, res.getIndoor(), LocalDateTime.now(), ssi);
            addChartPoint(ChartType.INDOOR_GF_TEMP, res.getIndoorGf(), LocalDateTime.now(), ssi);
            addChartPoint(ChartType.OUTDOOR_TEMP, res.getOutdoor(), LocalDateTime.now(), ssi);
            addChartPoint(ChartType.GARAGE_TEMP, res.getGarage(), LocalDateTime.now(), ssi);
        }
        res.getIndoor().sort(Comparator.comparing(ChartPoint::getDt));
        res.getIndoorGf().sort(Comparator.comparing(ChartPoint::getDt));
        res.getOutdoor().sort(Comparator.comparing(ChartPoint::getDt));
        res.getGarage().sort(Comparator.comparing(ChartPoint::getDt));
        return res;
    }

    public PressureStat getPressureStat() {
        PressureStat res = new PressureStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.PRESSURE, res.getPressure(), entry.getKey(), entry.getValue());
        }
        if (res.getPressure().isEmpty()) {
            addChartPoint(ChartType.PRESSURE, res.getPressure(), LocalDateTime.now(), new SystemSummaryInfo());
        }
        res.getPressure().sort(Comparator.comparing(ChartPoint::getDt));
        return res;
    }

    private void addChartPoint(ChartType type, List<ChartPoint> data, LocalDateTime ts, SystemSummaryInfo systemSummaryInfo) {
        switch (type) {
            case INDOOR_TEMP:
                data.add(new ChartPoint(ts, systemSummaryInfo.getSfTemperature()));
                break;
            case INDOOR_GF_TEMP:
                data.add(new ChartPoint(ts, systemSummaryInfo.getGfTemperature()));
                break;
            case OUTDOOR_TEMP:
                data.add(new ChartPoint(ts, systemSummaryInfo.getOutDoorTemperature()));
                break;
            case GARAGE_TEMP:
                data.add(new ChartPoint(ts, systemSummaryInfo.getGarageTemperature()));
                break;
            case PRESSURE:
                data.add(new ChartPoint(ts, systemSummaryInfo.getPressure()));
                break;
            case BOILER_TEMP:
                data.add(new ChartPoint(ts, systemSummaryInfo.getBoilerTemperature()));
                break;
            case LUMINOSITY:
                data.add(new ChartPoint(ts, systemSummaryInfo.getLuminosity()));
                break;
            case SYSTEM_LA:
                data.add(new ChartPoint(ts, systemSummaryInfo.getLoadAvg()));
                break;
            case SYSTEM_HEAP_MAX:
                data.add(new ChartPoint(ts, systemSummaryInfo.getHeapMax()));
                break;
            case SYSTEM_HEAP_USAGE:
                data.add(new ChartPoint(ts, systemSummaryInfo.getHeapUsage()));
                break;
        }
    }

    public BoilerTempStat getBoilerTempStat() {
        BoilerTempStat res = new BoilerTempStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.BOILER_TEMP, res.getTemperature(), entry.getKey(), entry.getValue());
        }
        if (res.getTemperature().isEmpty()) {
            addChartPoint(ChartType.BOILER_TEMP, res.getTemperature(), LocalDateTime.now(), new SystemSummaryInfo());
        }
        res.getTemperature().sort(Comparator.comparing(ChartPoint::getDt));
        return res;
    }

    public LuminosityStat getLuminosityStat() {
        LuminosityStat res = new LuminosityStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.LUMINOSITY, res.getLuminosity(), entry.getKey(), entry.getValue());
        }
        if (res.getLuminosity().isEmpty()) {
            addChartPoint(ChartType.LUMINOSITY, res.getLuminosity(), LocalDateTime.now(), new SystemSummaryInfo());
        }
        res.getLuminosity().sort(Comparator.comparing(ChartPoint::getDt));
        return res;
    }

    public SystemStat getSystemStat() {
        SystemStat res = new SystemStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);
        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.SYSTEM_HEAP_MAX, res.getHeapMax(), entry.getKey(), entry.getValue());
            addChartPoint(ChartType.SYSTEM_HEAP_USAGE, res.getHeapUsage(), entry.getKey(), entry.getValue());
        }
        if (res.getHeapMax().isEmpty()) {
            addChartPoint(ChartType.SYSTEM_HEAP_MAX, res.getHeapMax(), LocalDateTime.now(), new SystemSummaryInfo());
            addChartPoint(ChartType.SYSTEM_HEAP_USAGE, res.getHeapUsage(), LocalDateTime.now(), new SystemSummaryInfo());
        }
        res.getHeapMax().sort(Comparator.comparing(ChartPoint::getDt));
        res.getHeapUsage().sort(Comparator.comparing(ChartPoint::getDt));
        return res;
    }

    public LaStat getLaStat() {
        LaStat res = new LaStat();
        Map<LocalDateTime, SystemSummaryInfo> data = new HashMap<>(statCache);

        for (Map.Entry<LocalDateTime, SystemSummaryInfo> entry : data.entrySet()) {
            addChartPoint(ChartType.SYSTEM_LA, res.getLa(), entry.getKey(), entry.getValue());
        }

        if (res.getLa().isEmpty()) {
            addChartPoint(ChartType.SYSTEM_LA, res.getLa(), LocalDateTime.now(), new SystemSummaryInfo());
        }
        res.getLa().sort(Comparator.comparing(ChartPoint::getDt));
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
        SYSTEM_HEAP_USAGE
    }
}
