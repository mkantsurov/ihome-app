package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.ControllerType;
import technology.positivehome.ihome.server.persistence.model.ControllerConfigEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by maxim on 6/27/19.
 **/
@Component
public class ControllerConfigEntryRowMapper implements RowMapper<ControllerConfigEntity> {
    @Override
    public ControllerConfigEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ControllerConfigEntity(
                rs.getLong("id"),
                ControllerType.of(rs.getInt("type")),
                rs.getString("name"),
                rs.getString("ip_address"),
                rs.getInt("port"));
    }
}
