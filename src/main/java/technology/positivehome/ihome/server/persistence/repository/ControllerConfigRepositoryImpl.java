package technology.positivehome.ihome.server.persistence.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import technology.positivehome.ihome.server.persistence.mapper.ControllerConfigEntryRowMapper;
import technology.positivehome.ihome.server.persistence.model.ControllerConfigEntity;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Created by maxim on 2/26/23.
 **/
@Repository
public class ControllerConfigRepositoryImpl implements ControllerConfigRepository {
//long id, ControllerType type, String name, String ipAddr, int port)
    private static final String SELECT_CONTROLLERS = "SELECT id, type, name, ip_address, port FROM controller_config_entry ORDER BY id";
    private static final String GET_CONTROLLER_BY_ID = "SELECT id, type, name, ip_address, port FROM controller_config_entry ORDER BY id";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ControllerConfigEntryRowMapper controllerConfigEntryRowMapper;

    public ControllerConfigRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, ControllerConfigEntryRowMapper controllerConfigEntryRowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.controllerConfigEntryRowMapper = controllerConfigEntryRowMapper;
    }

    @Override
    public ControllerConfigEntity getById(@Nonnull Long id) {
        return namedParameterJdbcTemplate.queryForObject(
                GET_CONTROLLER_BY_ID,
                Map.of("id", id),
                controllerConfigEntryRowMapper);
    }

    @Override
    public Long create(@Nonnull ControllerConfigEntity entity) {
        return null;
    }

    @Override
    public void update(@Nonnull ControllerConfigEntity entity) {

    }

    @Override
    public void remove(@Nonnull Long id) {

    }

    @Override
    public List<ControllerConfigEntity> findAll() {
        return namedParameterJdbcTemplate.query(SELECT_CONTROLLERS, controllerConfigEntryRowMapper);
    }
}
