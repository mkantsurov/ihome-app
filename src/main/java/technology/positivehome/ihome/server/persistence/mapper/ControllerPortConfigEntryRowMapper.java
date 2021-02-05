package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.MegadPortType;
import technology.positivehome.ihome.domain.runtime.controller.ControllerPortConfigEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by maxim on 6/27/19.
 **/
@Component
public class ControllerPortConfigEntryRowMapper implements RowMapper<ControllerPortConfigEntity> {

    @Override
    public ControllerPortConfigEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ControllerPortConfigEntity res = new ControllerPortConfigEntity();
        res.setId(rs.getLong("id"));
        res.setPortAdress(rs.getInt("port_address"));
        res.setType(MegadPortType.values()[rs.getInt("type")]);
        res.setDescription(rs.getString("description"));
        res.setControllerId(rs.getLong("controller_id"));
        return res;
    }
}
