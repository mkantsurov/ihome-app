package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.ModuleDisplayMode;
import technology.positivehome.ihome.domain.constant.UiControlType;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by maxim on 6/28/19.
 **/
@Component
public class ModuleConfigElementEntryRowMapper implements RowMapper<ModuleConfigElementEntity> {
    @Override
    public ModuleConfigElementEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ModuleConfigElementEntity entry = new ModuleConfigElementEntity();
        entry.setId(rs.getLong("id"));
        entry.setName(rs.getString("name"));
        entry.setType(UiControlType.values()[rs.getInt("type")]);
        entry.setDisplayMode(ModuleDisplayMode.values()[rs.getInt("display_mode")]);
        entry.setPort(rs.getLong("port"));
        entry.setModuleId(rs.getLong("module_id"));
        return entry;
    }
}
