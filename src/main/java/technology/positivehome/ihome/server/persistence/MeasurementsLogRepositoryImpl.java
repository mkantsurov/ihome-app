package technology.positivehome.ihome.server.persistence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import technology.positivehome.ihome.domain.runtime.event.MeasurementLogEntity;
import technology.positivehome.ihome.server.persistence.mapper.MeasurementLogEntryRowMapper;

import java.time.LocalDateTime;
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
                    "indoor_sf_humidity, indoor_gf_temp, garage_temp, garage_humidity, boiler_temp, " +
                    "luminosity, ext_pwr_voltage, ext_pwr_current, ext_pwr_frequency, ext_pwr_consumption, " +
                    "int_pwr_voltage, int_pwr_current, int_pwr_frequency, int_pwr_consumption, " +
                    "security_mode, pw_src_converter_mode, pw_src_direct_mode, " +
                    "heating_pump_ff_mode, heating_pump_sf_mode) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_MEASUREMENT_LOG_ENTRY_FOR_PERIOD =
            "SELECT id, created, load_avg, memory_heap_max, memory_heap_used, pressure, outdoor_temp, outdoor_humidity, " +
                    "indoor_sf_temp, indoor_sf_humidity, indoor_gf_temp, garage_temp, garage_humidity, boiler_temp, luminosity, " +
                    "ext_pwr_voltage, ext_pwr_current, ext_pwr_frequency, ext_pwr_consumption, " +
                    "int_pwr_voltage, int_pwr_current, int_pwr_frequency, int_pwr_consumption, " +
                    "security_mode, pw_src_converter_mode, pw_src_direct_mode, heating_pump_ff_mode, heating_pump_sf_mode " +
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
    public void writeLogEntry(MeasurementLogEntity logEntry) {
        jdbcTemplate.update(ADD_MEASUREMENT_LOG_ENTRY,
                logEntry.loadAvg(),
                logEntry.heapMax(),
                logEntry.heapUsage(),
                logEntry.pressure(),
                logEntry.outdoorTemp(),
                logEntry.outdoorHumidity(),
                logEntry.indoorSfTemp(),
                logEntry.indoorSfHumidity(),
                logEntry.indoorGfTemp(),
                logEntry.garageTemp(),
                logEntry.garageHumidity(),
                logEntry.boilerTemperature(),
                logEntry.luminosity(),
                logEntry.extPwrVoltage(),
                logEntry.extPwrCurrent(),
                logEntry.extPwrFrequency(),
                logEntry.extPwrConsumption(),
                logEntry.intPwrVoltage(),
                logEntry.intPwrCurrent(),
                logEntry.intPwrFrequency(),
                logEntry.intPwrConsumption(),
                logEntry.securityMode(),
                logEntry.pwSrcConverterMode(),
                logEntry.pwSrcDirectMode(),
                logEntry.heatingPumpFFMode(),
                logEntry.heatingPumpSFMode()
        );
    }

    @Override
    public List<MeasurementLogEntity> readDataForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return namedParameterJdbcTemplate.query(SELECT_MEASUREMENT_LOG_ENTRY_FOR_PERIOD,
                Map.of("start_time", startDate, "end_time", endDate), measurementLogEntryRowMapper);
    }

}
