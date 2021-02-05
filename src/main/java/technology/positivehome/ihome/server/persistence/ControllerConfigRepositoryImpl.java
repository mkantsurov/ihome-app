package technology.positivehome.ihome.server.persistence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import technology.positivehome.ihome.domain.runtime.controller.*;
import technology.positivehome.ihome.server.persistence.mapper.ControllerConfigEntryRowMapper;
import technology.positivehome.ihome.server.persistence.mapper.ControllerPortConfigEntryRowMapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by maxim on 6/27/19.
 **/
@Repository
public class ControllerConfigRepositoryImpl implements ControllerConfigRepository {

    private static final String SELECT_CONTROLLERS = "SELECT id, ip_address, name FROM controller_config_entry ORDER BY id";
    private static final String SELECT_CONTROLLER_PORT_CONFIG = "SELECT id, controller_id, port_address, type, description FROM controller_port_config_entry ORDER BY id";
    private static final String SELECT_CONTROLLER_BY_ID = "SELECT id, ip_address, name FROM controller_config_entry WHERE id = :id";
    private static final String SELECT_CONTROLLER_PORT_CONFIG_BY_CONTROLLER_ID = "SELECT id, controller_id, port_address, type, description FROM controller_port_config_entry WHERE controller_id = :controller_id ORDER BY id";
    private static final String UPDATE_CONTROLLER_PROPERTIES = "UPDATE controller_config_entry SET  name = :name, ip_address = :ip_address  WHERE id = :id";
    private static final String CREATE_NEW_CONTROLLER_PORT = "INSERT INTO controller_port_config_entry " +
            "(controller_id, port_address, type, description) " +
            "VALUES (:controller_id, :port_address, :type, :description)";
    private static final String DELETE_CONTROLLER_PORT =
            "DELETE FROM controller_port_config_entry WHERE id = :id";

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final ControllerConfigEntryRowMapper controllerConfigEntryRowMapper;
    private final ControllerPortConfigEntryRowMapper controllerPortConfigEntryRowMapper;

    public ControllerConfigRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                          ControllerConfigEntryRowMapper controllerConfigEntryRowMapper,
                                          ControllerPortConfigEntryRowMapper controllerPortConfigEntryRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.controllerConfigEntryRowMapper = controllerConfigEntryRowMapper;
        this.controllerPortConfigEntryRowMapper = controllerPortConfigEntryRowMapper;
    }

    @Override
    public List<ControllerConfigEntry> loadControllerConfig() {

        List<ControllerConfigEntry> result = jdbcTemplate.query(SELECT_CONTROLLERS, controllerConfigEntryRowMapper);
        List<ControllerPortConfigEntity> portConfigEntries = jdbcTemplate.query(SELECT_CONTROLLER_PORT_CONFIG, controllerPortConfigEntryRowMapper);

        Map<Long, List<ControllerPortConfigEntry>> portConf = new HashMap<>();
        for (ControllerPortConfigEntity entity : portConfigEntries) {
            List<ControllerPortConfigEntry> portList = portConf.computeIfAbsent(entity.getControllerId(), k -> new ArrayList<>());
            portList.add(entity);
        }

        for (ControllerConfigEntry entry : result) {
            List<ControllerPortConfigEntry> portList = portConf.get(entry.getId());
            entry.setPortConfig(portList != null ? portList : new ArrayList<>());
        }
        return result;
    }

    @Override
    public ControllerConfigEntry updateControllerProps(ControllerProperties property) {
        namedParameterJdbcTemplate.update(UPDATE_CONTROLLER_PROPERTIES,
                Map.of("id", property.getId(), "name", property.getControllerName(), "ip_address", property.getIpAddress()));
        ControllerConfigEntry res = namedParameterJdbcTemplate.queryForObject(SELECT_CONTROLLER_BY_ID,
                Map.of("id", property.getId()), controllerConfigEntryRowMapper);

        List<ControllerPortConfigEntity> portConfigEntries = namedParameterJdbcTemplate.query(SELECT_CONTROLLER_PORT_CONFIG_BY_CONTROLLER_ID, Map.of("controller_id", property.getId()), controllerPortConfigEntryRowMapper);
        Objects.requireNonNull(res, "Controller config entry #" + property.getId() + " does not exist.")
                .setPortConfig(portConfigEntries.stream().map(entity -> new ControllerPortConfigEntry(entity.getId(), entity.getDescription(), entity.getType(), entity.getPortAdress())).collect(Collectors.toList()));
        return res;
    }

    @Override
    public ControllerPortConfigEntry updateControllerPort(ControllerPortConfigEntry value) {
        namedParameterJdbcTemplate.update(UPDATE_CONTROLLER_PROPERTIES,
                Map.of("id", value.getId(), "port_address", value.getPortAdress(),
                        "type", value.getType().ordinal(), "description", value.getDescription()));
        ControllerPortConfigEntity res = namedParameterJdbcTemplate.queryForObject(SELECT_CONTROLLER_PORT_CONFIG_BY_CONTROLLER_ID, Map.of("id", value.getId()), controllerPortConfigEntryRowMapper);
        return new ControllerPortConfigEntry(res.getId(), res.getDescription(), res.getType(), res.getPortAdress());
    }

    @Override
    public List<ControllerPortConfigEntry> addNewControllerPort(AppendControllerPortConfigEntryRequest value) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("controller_id", value.getControllerId());
        namedParameters.addValue("port_address", value.getPortAdress());
        namedParameters.addValue("type", value.getType().ordinal());
        namedParameters.addValue("description", value.getDescription());

        namedParameterJdbcTemplate.update(CREATE_NEW_CONTROLLER_PORT,
                namedParameters,
                keyHolder,
                new String[]{"id"});

        List<ControllerPortConfigEntity> portConfigEntries = namedParameterJdbcTemplate.query(SELECT_CONTROLLER_PORT_CONFIG_BY_CONTROLLER_ID, Map.of("controller_id", value.getControllerId()), controllerPortConfigEntryRowMapper);
        return portConfigEntries.stream().map(entity -> new ControllerPortConfigEntry(entity.getId(), entity.getDescription(), entity.getType(), entity.getPortAdress())).collect(Collectors.toList());
    }

    @Override
    public List<ControllerPortConfigEntry> removeControllerPort(RemoveControllerPortRequest value) {
        namedParameterJdbcTemplate.update(DELETE_CONTROLLER_PORT, new MapSqlParameterSource("id", value.getPortId()));
        List<ControllerPortConfigEntity> portConfigEntries = namedParameterJdbcTemplate.query(SELECT_CONTROLLER_PORT_CONFIG_BY_CONTROLLER_ID, Map.of("controller_id", value.getControllerId()), controllerPortConfigEntryRowMapper);
        return portConfigEntries.stream().map(entity -> new ControllerPortConfigEntry(entity.getId(), entity.getDescription(), entity.getType(), entity.getPortAdress())).collect(Collectors.toList());
    }

}
