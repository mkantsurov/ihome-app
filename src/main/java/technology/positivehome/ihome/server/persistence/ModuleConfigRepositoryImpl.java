package technology.positivehome.ihome.server.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.constant.ModuleStartupMode;
import technology.positivehome.ihome.domain.runtime.module.*;
import technology.positivehome.ihome.server.persistence.mapper.ModuleConfigElementEntryRowMapper;
import technology.positivehome.ihome.server.persistence.mapper.ModuleConfigEntryRowMapper;
import technology.positivehome.ihome.server.persistence.mapper.ModuleGroupEntryRowMapper;
import technology.positivehome.ihome.server.persistence.mapper.ModulePropertyValueRowMapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by maxim on 6/27/19.
 **/
@Repository
public class ModuleConfigRepositoryImpl implements ModuleConfigRepository {

    private static final String SELECT_MODULES = "SELECT me.id, me.module_assignment, me.mode, me.startup_mode, me.name, me.display, me.type, mg.id group_id, mg.name group_name, mg.priority FROM module_config_entry me INNER JOIN module_group_entry mg ON (me.group_id = mg.id) ORDER BY id";
    private static final String SELECT_MODULE_CONFIG_ENTRIES = "SELECT id, module_id, name, type, port, display_mode FROM module_config_element_entry ORDER BY id";
    private static final String SELECT_MODULE_PROPERTY_ENTRIES = "SELECT id, module_id, key, long_value, string_value FROM module_property_entry ORDER BY id";

    private static final String SELECT_MODULE_BY_ID = "SELECT me.id, me.mode, me.startup_mode, me.name, me.module_assignment, me.display, me.type, mg.id group_id, mg.name group_name, mg.priority FROM module_config_entry me INNER JOIN module_group_entry mg ON (me.group_id = mg.id) WHERE me.id = :id ORDER BY id";
    private static final String SELECT_MODULE_CONFIG_ENTRIES_BY_MODULE_ID = "SELECT id, module_id, name, type, port, display_mode FROM module_config_element_entry WHERE module_id = :module_id ORDER BY id";
    private static final String SELECT_MODULE_PROPERTY_ENTRIES_BY_MODULE_ID = "SELECT id, module_id, key, long_value, string_value FROM module_property_entry WHERE module_id = :module_id ORDER BY id";

    private static final String SELECT_MODULE_GROUP_ENTRY_BY_ID = "SELECT id, name, priority FROM module_group_entry WHERE id = :id";
    private static final String UPDATE_MODULE_MODE = "UPDATE module_config_entry SET mode = :mode WHERE id = :id";
    private static final String UPDATE_MODULE_STARTUP_MODE = "UPDATE module_config_entry SET startup_mode = :startupMode WHERE id = :id";
    private static final String CREATE_NEW_MODULE = "INSERT INTO module_config_entry " +
            "(mode, module_assignment, name, group_id, type) " +
            "VALUES (:mode, :moduleAssigment, :name, :group_id, :type)";

    private static final String UPDATE_MODULE_SETTINGS = "UPDATE module_config_entry SET mode = :mode, name = :name, group_id = :group_id WHERE id = :id";
    private static final String UPDATE_CONFIG_ELEMENT_ENTRY = "UPDATE module_config_element_entry SET  name = :name, type = :type, port = :port, display_mode = :display_mode  WHERE id = :id";
    private static final String SELECT_MODULE_CONFIG_ENTRY_BY_ID = "SELECT id, module_id, name, type, port, display_mode FROM module_config_element_entry WHERE id = :id";

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final ModuleConfigEntryRowMapper moduleConfigEntryRowMapper;
    private final ModuleConfigElementEntryRowMapper moduleConfigElementEntryRowMapper;
    private final ModulePropertyValueRowMapper modulePropertyValueRowMapper;
    private final ModuleGroupEntryRowMapper moduleGroupEntryRowMapper;

    @Autowired
    public ModuleConfigRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate, ModuleConfigEntryRowMapper moduleConfigEntryRowMapper, ModuleConfigElementEntryRowMapper moduleConfigElementEntryRowMapper, ModulePropertyValueRowMapper modulePropertyValueRowMapper, ModuleGroupEntryRowMapper moduleGroupEntryRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.moduleConfigEntryRowMapper = moduleConfigEntryRowMapper;
        this.moduleConfigElementEntryRowMapper = moduleConfigElementEntryRowMapper;
        this.modulePropertyValueRowMapper = modulePropertyValueRowMapper;
        this.moduleGroupEntryRowMapper = moduleGroupEntryRowMapper;
    }

    @Override
    public List<ModuleConfigEntry> loadModuleConfig() {

        List<ModuleConfigEntry> result = jdbcTemplate.query(SELECT_MODULES, moduleConfigEntryRowMapper);
        List<ModuleConfigElementEntity> moduleConfigEntries = jdbcTemplate.query(SELECT_MODULE_CONFIG_ENTRIES, moduleConfigElementEntryRowMapper);
        List<ModulePropertyEntity> modulePropertyValues = jdbcTemplate.query(SELECT_MODULE_PROPERTY_ENTRIES, modulePropertyValueRowMapper);
//        List<ModuleGroupEntry> moduleGroupEntries = jdbcTemplate.queryForList(SELECT_MODULE_GROUP_ENTRIES, null, moduleGroupEntryRowMapper);

        Map<Long, List<ModuleConfigElementEntry>> moduleConf = new HashMap<>();
        for (ModuleConfigElementEntity entity : moduleConfigEntries) {
            List<ModuleConfigElementEntry> confList = moduleConf.computeIfAbsent(entity.getModuleId(), k -> new ArrayList<>());
            confList.add(entity);
        }

        Map<Long, List<ModulePropertyValue>> moduleProps = new HashMap<>();
        for (ModulePropertyEntity entity : modulePropertyValues) {
            List<ModulePropertyValue> propList = moduleProps.computeIfAbsent(entity.getModuleId(), k -> new ArrayList<>());
            propList.add(entity);
        }

        for (ModuleConfigEntry entry : result) {
            List<ModuleConfigElementEntry> confList = moduleConf.get(entry.getId());
            List<ModulePropertyValue> propList = moduleProps.get(entry.getId());
            entry.setControls(confList != null ? confList : new ArrayList<>());
            entry.setProperties(propList != null ? propList : new ArrayList<>());
        }
        return result;
    }

    @Override
    public ModuleConfigEntry updateModuleMode(long moduleId, ModuleOperationMode newMode) {
        namedParameterJdbcTemplate.update(UPDATE_MODULE_MODE,
                Map.of("id", moduleId, "mode", newMode.ordinal()));

        return loadModuleData(moduleId);
    }

    public ModuleConfigEntry updateModuleStartupMode(long moduleId, ModuleStartupMode moduleStartupMode) {
        namedParameterJdbcTemplate.update(UPDATE_MODULE_STARTUP_MODE,
                Map.of("id", moduleId, "startupMode", moduleStartupMode.ordinal()));
        return loadModuleData(moduleId);
    }

    @Override
    public List<ModuleConfigEntry> addNewModule(ModuleSettings moduleSettings) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("mode", moduleSettings.getMode().ordinal());
        namedParameters.addValue("name", moduleSettings.getModuleName());
        namedParameters.addValue("group_id", moduleSettings.getModuleGroupEntryId());
        namedParameters.addValue("type", moduleSettings.getType().ordinal());

        namedParameterJdbcTemplate.update(CREATE_NEW_MODULE,
                namedParameters,
                keyHolder,
                new String[]{"id"});
        return loadModuleConfig();
    }

    @Override
    public ModuleConfigEntry updateModuleProperties(ModuleSettings moduleSettings) {
        namedParameterJdbcTemplate.update(UPDATE_MODULE_SETTINGS,
                Map.of("id", moduleSettings.getId(),
                        "mode", moduleSettings.getMode().ordinal(),
                        "name", moduleSettings.getModuleName(),
                        "group_id", moduleSettings.getModuleGroupEntryId()));
        return loadModuleData(moduleSettings.getId());
    }

    @Override
    public ModuleConfigElementEntry updateModuleConfigElement(ModuleConfigElementEntry value) {
        namedParameterJdbcTemplate.update(UPDATE_CONFIG_ELEMENT_ENTRY,
                Map.of("id", value.getId(),
                        "name", value.getName(),
                        "type", value.getType().ordinal(),
                        "port", value.getPort(),
                        "display_mode", value.getDisplayMode().ordinal()));
        ModuleConfigElementEntry res = namedParameterJdbcTemplate.queryForObject(SELECT_MODULE_CONFIG_ENTRY_BY_ID,
                Map.of("id", value.getId()), moduleConfigElementEntryRowMapper);
        return Objects.requireNonNull(res, "Module config entry #" + value.getId() + " does not exist.");
    }

    private ModuleConfigEntry loadModuleData(long moduleId) {
        ModuleConfigEntry res = namedParameterJdbcTemplate.queryForObject(SELECT_MODULE_BY_ID,
                Map.of("id", moduleId), moduleConfigEntryRowMapper);

        List<ModuleConfigElementEntity> moduleConfigEntries = namedParameterJdbcTemplate.query(SELECT_MODULE_CONFIG_ENTRIES_BY_MODULE_ID, Map.of("module_id", moduleId), moduleConfigElementEntryRowMapper);
        List<ModulePropertyEntity> modulePropertyValues = namedParameterJdbcTemplate.query(SELECT_MODULE_PROPERTY_ENTRIES_BY_MODULE_ID, Map.of("module_id", moduleId), modulePropertyValueRowMapper);
        Objects.requireNonNull(res, "Module entry #" + moduleId + " does not exist.")
                .setControls(moduleConfigEntries.stream().map(entity -> new ModuleConfigElementEntry(entity.getId(), entity.getName(), entity.getType(), entity.getDisplayMode(), entity.getPort())).collect(Collectors.toList()));
        Objects.requireNonNull(res, "Module entry #" + moduleId + " does not exist.")
                .setProperties(modulePropertyValues.stream().map(entity -> new ModulePropertyValue(entity.getId(), entity.getKey(), entity.getStringValue(), entity.getLongValue())).collect(Collectors.toList()));
        return res;
    }

}
