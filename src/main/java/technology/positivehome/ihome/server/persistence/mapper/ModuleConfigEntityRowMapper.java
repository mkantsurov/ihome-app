package technology.positivehome.ihome.server.persistence.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.model.constant.ModuleAssignment;
import technology.positivehome.ihome.model.constant.ModuleOperationMode;
import technology.positivehome.ihome.model.constant.ModuleStartupMode;
import technology.positivehome.ihome.model.constant.ModuleType;
import technology.positivehome.ihome.model.runtime.module.ModuleConfigEntity;
import technology.positivehome.ihome.model.runtime.module.ModuleGroupEntry;
import technology.positivehome.ihome.security.model.user.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * RowMapper that returns {@link ModuleConfigEntity} (a record with raw {@code groupId} FK).
 * The repository uses this internally and converts to
 * {@link technology.positivehome.ihome.model.runtime.module.ModuleConfigEntry} at the boundary.
 */
@Component
public class ModuleConfigEntityRowMapper implements RowMapper<ModuleConfigEntity> {

    private final ObjectMapper objectMapper;

    public ModuleConfigEntityRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ModuleConfigEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ModuleGroupEntry groupEntry = new ModuleGroupEntry();
        groupEntry.setId(rs.getLong("group_id"));
        groupEntry.setName(rs.getString("group_name"));
        groupEntry.setPriority(rs.getInt("priority"));

        String permissionJson = rs.getString("permission");
        List<Role> writerRoleNames = parseWriterRoleNames(permissionJson);

        return ModuleConfigEntity.builder()
                .id(rs.getLong("id"))
                .moduleName(rs.getString("name"))
                .moduleAssignment(ModuleAssignment.values()[rs.getInt("module_assignment")])
                .mode(ModuleOperationMode.values()[rs.getInt("mode")])
                .startupMode(ModuleStartupMode.values()[rs.getInt("startup_mode")])
                .type(ModuleType.values()[rs.getInt("type")])
                .writerRoleNames(writerRoleNames)
                .groupId(rs.getLong("group_id"))
                .moduleGroupEntry(groupEntry)
                .build();
    }

    private List<Role> parseWriterRoleNames(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Role>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
