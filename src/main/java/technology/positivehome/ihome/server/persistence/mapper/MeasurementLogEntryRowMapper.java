package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.runtime.event.MeasurementLogEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by maxim on 6/27/19.
 **/
@Component
public class MeasurementLogEntryRowMapper implements RowMapper<MeasurementLogEntry> {
    @Override
    public MeasurementLogEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new MeasurementLogEntry(
                rs.getLong("id"),
                new Date(rs.getTimestamp("created").getTime()),
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
                rs.getInt("power_stat"));
    }
}
