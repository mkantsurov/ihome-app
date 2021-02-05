package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.ModuleProperty;
import technology.positivehome.ihome.domain.runtime.module.ModulePropertyEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by maxim on 6/28/19.
 **/
@Component
public class ModulePropertyValueRowMapper implements RowMapper<ModulePropertyEntity> {

    @Override
    public ModulePropertyEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ModulePropertyEntity result = new ModulePropertyEntity();
        result.setId(rs.getLong("id"));
        result.setKey(ModuleProperty.values()[rs.getInt("key")]);
        result.setStringValue(rs.getString("string_value"));
        result.setLongValue(rs.getLong("long_value"));
        result.setModuleId(rs.getLong("module_id"));
        return result;
    }
}
