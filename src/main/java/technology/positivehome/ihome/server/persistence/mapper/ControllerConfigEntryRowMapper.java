package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.runtime.controller.ControllerConfigEntry;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by maxim on 6/27/19.
 **/
@Component
public class ControllerConfigEntryRowMapper implements RowMapper<ControllerConfigEntry> {
    @Override
    public ControllerConfigEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        ControllerConfigEntry entry = new ControllerConfigEntry();
        entry.setId(rs.getLong("id"));
        entry.setIpAddress(rs.getString("ip_address"));
        entry.setControllerName(rs.getString("name"));
        return entry;
    }
}
