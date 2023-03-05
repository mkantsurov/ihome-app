package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.IHomePortType;
import technology.positivehome.ihome.server.persistence.model.ControllerPortConfigEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by maxim on 6/27/19.
 **/
@Component
public class ControllerPortConfigEntryRowMapper implements RowMapper<ControllerPortConfigEntity> {

    @Override
    public ControllerPortConfigEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new ControllerPortConfigEntity(
                rs.getLong("id"),
                rs.getLong("controller_id"),
                IHomePortType.of(rs.getInt("type")),
                rs.getInt("port_address"),
                rs.getString("description")
                );
    }
}
