package technology.positivehome.ihome.server.persistence.repository.queryexec;

import jakarta.annotation.Nullable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import technology.positivehome.ihome.domain.constant.ErrorMessageLogSortRule;
import technology.positivehome.ihome.server.model.SearchParam;
import technology.positivehome.ihome.server.persistence.mapper.ErrorMessageLogRowMapper;
import technology.positivehome.ihome.server.persistence.model.ErrorMessageLogEntity;

import java.util.List;
import java.util.Optional;

public class ErrorMessageLogQueryExecutorImpl implements ErrorMessageLogQueryExecutor,  ErrorMessageLogQueryExecutor.PagingAndExecuteSpec {
    //language=SQL
    public static final String ERROR_MESSAGE_LOG_QUERY_BODY = """
            SELECT
                log.id, log.created, log.type, log.message
            FROM error_message_log log
            """;
    //language=SQL
    public static final String ERROR_MESSAGE_LOG_COUNT_QUERY_BODY = """
            SELECT count(*) FROM error_message_log log
            """;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ErrorMessageLogRowMapper errorMessageLogRowMapper;
    private final MapSqlParameterSource paramSrc;
    private final StringBuilder sql;
    private final StringBuilder countSql;

    private ErrorMessageLogQueryExecutorImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                             ErrorMessageLogRowMapper errorMessageLogRowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.errorMessageLogRowMapper = errorMessageLogRowMapper;
        this.sql = new StringBuilder(ERROR_MESSAGE_LOG_QUERY_BODY);
        this.countSql = new StringBuilder(ERROR_MESSAGE_LOG_COUNT_QUERY_BODY);
        this.paramSrc = new MapSqlParameterSource();
    }

    public static ErrorMessageLogQueryExecutor getInstance(NamedParameterJdbcTemplate namedParameterJdbcTemplate, ErrorMessageLogRowMapper errorMessageLogRowMapper) {
        return new ErrorMessageLogQueryExecutorImpl(namedParameterJdbcTemplate, errorMessageLogRowMapper);
    }
    @Override
    public ErrorMessageLogQueryExecutor filter(@Nullable List<SearchParam> filters) {
        return this;
    }
    @Override
    public PagingAndExecuteSpec order(@Nullable List<ErrorMessageLogSortRule> sortRules) {
        sql.append(" ORDER BY ");
        if (sortRules == null || sortRules.isEmpty()) {
            sql.append(" created ");
            return this;
        }
        String separator = "";

        for (ErrorMessageLogSortRule rule : sortRules) {
            separator = switch (rule) {
                case CREATED_ASC -> {
                    sql.append(separator).append(" log.created ");
                    yield ", ";
                }
                case CREATED_DESC -> {
                    sql.append(separator).append(" log.created DESC ");
                    yield ", ";
                }
                default -> separator;
            };
        }
        return this;
    }
    @Override
    public ExecuteSpec page(@Nullable Integer page, @Nullable Integer size) {
        if (page != null && size != null) {
            int offset = page * size;
            sql.append(" OFFSET :offset LIMIT :limit ");
            paramSrc.addValue("offset", offset);
            paramSrc.addValue("limit", size);
        }
        return this;
    }

    @Override
    public long getCount() {
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(countSql.toString(), paramSrc, Long.class)).orElseThrow();
    }
    @Override
    public List<ErrorMessageLogEntity> executeQuery() {
        return namedParameterJdbcTemplate.query(
                sql.toString(),
                paramSrc,
                errorMessageLogRowMapper
        );
    }

}
