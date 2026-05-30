package technology.positivehome.ihome.server.persistence.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import technology.positivehome.ihome.server.persistence.mapper.ControllerPortConfigEntryRowMapper;
import technology.positivehome.ihome.server.persistence.model.ControllerPortConfigEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Created by maxim on 2/26/23.
 **/
@Repository
public class ControllerPortConfigRepositoryImpl implements ControllerPortConfigRepository {
    private static final String SELECT_CONTROLLER_PORT_CONFIG_BY_CONTROLLER_ID = "SELECT id, controller_id, port_address, type, description FROM controller_port_config_entry WHERE controller_id = :controller_id ORDER BY id";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ControllerPortConfigEntryRowMapper controllerPortConfigEntryRowMapper;

    public ControllerPortConfigRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, ControllerPortConfigEntryRowMapper controllerPortConfigEntryRowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.controllerPortConfigEntryRowMapper = controllerPortConfigEntryRowMapper;
    }

    @Override
    public ControllerPortConfigEntity getById(@Nonnull Long id) {
        return null;
    }

    @Override
    public Long create(@Nonnull ControllerPortConfigEntity entity) {
        return null;
    }

    @Override
    public void update(@Nonnull ControllerPortConfigEntity entity) {

    }

    @Override
    public void remove(@Nonnull Long id) {

    }

    @Override
    public List<ControllerPortConfigEntity> findByControllerId(long id) {
        return namedParameterJdbcTemplate.query(SELECT_CONTROLLER_PORT_CONFIG_BY_CONTROLLER_ID,
                Map.of("controller_id", id), controllerPortConfigEntryRowMapper);
    }

}
