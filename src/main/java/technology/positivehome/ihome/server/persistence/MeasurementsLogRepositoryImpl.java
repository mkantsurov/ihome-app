package technology.positivehome.ihome.server.persistence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import technology.positivehome.ihome.domain.runtime.event.MeasurementLogEntry;
import technology.positivehome.ihome.server.persistence.mapper.MeasurementLogEntryRowMapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by maxim on 9/1/19.
 **/
@Repository
public class MeasurementsLogRepositoryImpl implements MeasurementsLogRepository {

    private static final String ADD_MEASUREMENT_LOG_ENTRY =
            "INSERT INTO measurements_log_entry (" +
                    "load_avg, memory_heap_max, memory_heap_used, " +
                    "pressure,outdoor_temp,outdoor_humidity,indoor_sf_temp," +
                    "indoor_sf_humidity,indoor_gf_temp,garage_temp,garage_humidity,boiler_temp, luminosity) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_MEASUREMENT_LOG_ENTRY_FOR_PERIOD =
            "SELECT id, created, load_avg, memory_heap_max, memory_heap_used, pressure, outdoor_temp, outdoor_humidity, indoor_sf_temp, indoor_sf_humidity, indoor_gf_temp, garage_temp, garage_humidity, boiler_temp, luminosity " +
                    "FROM measurements_log_entry " +
                    "WHERE created >= :start_time AND created < :end_time";

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final MeasurementLogEntryRowMapper measurementLogEntryRowMapper;

    public MeasurementsLogRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate, MeasurementLogEntryRowMapper measurementLogEntryRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.measurementLogEntryRowMapper = measurementLogEntryRowMapper;
    }

    @Override
    public void writeLogEntry(MeasurementLogEntry logEntry) {
        jdbcTemplate.update(ADD_MEASUREMENT_LOG_ENTRY,
                logEntry.getLoadAvg(),
                logEntry.getHeapMax(),
                logEntry.getHeapUsage(),
                logEntry.getPressure(),
                logEntry.getOutdoorTemp(),
                logEntry.getOutdoorHumidity(),
                logEntry.getIndoorSfTemp(),
                logEntry.getIndoorSfHumidity(),
                logEntry.getIndoorGfTemp(),
                logEntry.getGarageTemp(),
                logEntry.getGarageHumidity(),
                logEntry.getBoilerTemperature(),
                logEntry.getLuminosity());
    }

    @Override
    public List<MeasurementLogEntry> readDataForPeriod(Date startDate, Date endDate) {
        return namedParameterJdbcTemplate.query(SELECT_MEASUREMENT_LOG_ENTRY_FOR_PERIOD, Map.of("start_time", startDate, "end_time", endDate), measurementLogEntryRowMapper);
    }

}
