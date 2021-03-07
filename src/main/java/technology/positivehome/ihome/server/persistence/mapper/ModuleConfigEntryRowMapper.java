package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.ModuleAssignment;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.constant.ModuleType;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleGroupEntry;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by maxim on 6/28/19.
 **/
@Component
public class ModuleConfigEntryRowMapper implements RowMapper<ModuleConfigEntry> {

    @Override
    public ModuleConfigEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        ModuleConfigEntry result = new ModuleConfigEntry();
        result.setId(rs.getLong("id"));
        result.setModuleName(rs.getString("name"));
        result.setModuleAssignment(ModuleAssignment.values()[rs.getInt("module_assignment")]);
        result.setMode(ModuleOperationMode.values()[rs.getInt("mode")]);
        result.setType(ModuleType.values()[rs.getInt("type")]);
        result.setModuleGroupEntry(new ModuleGroupEntry());
        result.getModuleGroupEntry().setId(rs.getLong("group_id"));
        result.getModuleGroupEntry().setName(rs.getString("group_name"));
        result.getModuleGroupEntry().setPriority(rs.getInt("priority"));
        return result;
    }
}
