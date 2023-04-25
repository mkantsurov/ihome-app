package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.runtime.event.MeasurementLogEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Created by maxim on 6/27/19.
 **/
@Component
public class MeasurementLogEntryRowMapper implements RowMapper<MeasurementLogEntity> {
    @Override
    public MeasurementLogEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new MeasurementLogEntity(
                rs.getLong("id"),
                rs.getObject("created", LocalDateTime.class),
                rs.getInt("load_avg"),
                rs.getInt("memory_heap_max"),
                rs.getInt("memory_heap_used"),
                rs.getInt("pressure"),
                rs.getInt("outdoor_temp"),
                rs.getInt("outdoor_humidity"),
                rs.getInt("indoor_sf_temp"),
                rs.getInt("indoor_sf_humidity"),
                rs.getInt("indoor_gf_temp"),
                rs.getInt("garage_temp"),
                rs.getInt("garage_humidity"),
                rs.getInt("boiler_temp"),
                rs.getInt("luminosity"),
                rs.getInt("ext_pwr_voltage"),
                rs.getInt("security_mode"),
                rs.getInt("pw_src_converter_mode"),
                rs.getInt("pw_src_direct_mode"),
                rs.getInt("heating_pump_ff_mode"),
                rs.getInt("heating_pump_sf_mode")
        );
    }
}
