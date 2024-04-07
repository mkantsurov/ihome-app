package technology.positivehome.ihome.server.persistence.repository;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import technology.positivehome.ihome.domain.constant.ErrorMessageLogSortRule;
import technology.positivehome.ihome.server.model.SearchParam;
import technology.positivehome.ihome.server.persistence.mapper.ErrorMessageLogRowMapper;
import technology.positivehome.ihome.server.persistence.model.ErrorMessageLogEntity;
import technology.positivehome.ihome.server.persistence.repository.queryexec.ErrorMessageLogQueryExecutorImpl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ErrorMessageLogRepositoryImpl implements ErrorMessageLogRepository {
    private static final String GET_BY_ID = """
            SELECT id, created, type, message
            FROM error_message_log WHERE id=:id
            """;

    private static final String ADD_ENTITY = """
            INSERT INTO error_message_log (id, created, type, message)
            VALUES (:id, :created, :type, :message)
            """;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ErrorMessageLogRowMapper errorMessageLogRowMapper;
    private final EthernetAddress ethernetAddress;

    public ErrorMessageLogRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, ErrorMessageLogRowMapper errorMessageLogRowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.errorMessageLogRowMapper = errorMessageLogRowMapper;
        this.ethernetAddress = EthernetAddress.fromInterface();
    }

    @Override
    public ErrorMessageLogEntity getById(@Nonnull UUID id) {
        return namedParameterJdbcTemplate.queryForObject(
            GET_BY_ID,
            Map.of("id", id),
            errorMessageLogRowMapper);
    }

    @Override
    public UUID create(@Nonnull ErrorMessageLogEntity entity) {
        UUID newId = (Generators.timeBasedGenerator(ethernetAddress)).generate();
        MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", newId);
        namedParameterJdbcTemplate.update(ADD_ENTITY, namedParameters);
        return newId;
    }

    @Override
    public void update(@Nonnull ErrorMessageLogEntity entity) {
        throw new NotImplementedException("Ability to update ErrorMessageLog entry not supported");
    }

    @Override
    public void remove(@Nonnull UUID id) {
        throw new NotImplementedException("Ability to remove ErrorMessageLog entry not supported");
    }

    @Override
    public List<ErrorMessageLogEntity> searchErrorMessages(@Nullable List<SearchParam> filter, @Nullable Integer page, @Nullable Integer size, @Nullable List<ErrorMessageLogSortRule> sort) {
        return ErrorMessageLogQueryExecutorImpl.getInstance(namedParameterJdbcTemplate, errorMessageLogRowMapper)
                .filter(filter)
                .order(sort)
                .page(page, size)
                .executeQuery();
    }

    @Override
    public long countUserAuditLogEntries(@Nullable List<SearchParam> filter) {
        return ErrorMessageLogQueryExecutorImpl.getInstance(namedParameterJdbcTemplate, errorMessageLogRowMapper)
                .filter(filter)
                .getCount();
    }

}
