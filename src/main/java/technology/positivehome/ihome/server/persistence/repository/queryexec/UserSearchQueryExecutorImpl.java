package technology.positivehome.ihome.server.persistence.repository.queryexec;

import jakarta.annotation.Nullable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import technology.positivehome.ihome.model.constant.SearchField;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.server.model.SearchParam;
import technology.positivehome.ihome.server.persistence.mapper.UserRowMapper;
import technology.positivehome.ihome.server.persistence.model.UserEntity;

import java.util.*;

public class UserSearchQueryExecutorImpl implements UserSearchQueryExecutor, UserSearchQueryExecutor.PagingAndExecuteSpec {

    //language=SQL
    private static final String USER_QUERY_BODY = """
            SELECT u.id, u.username, u.password, ur.role
            FROM user_entry u
            """;

    //language=SQL
    private static final String USER_COUNT_QUERY_BODY = """
            SELECT count(DISTINCT u.id) FROM user_entry u
            """;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserRowMapper.UserWithRolesExtractor userWithRolesExtractor;
    private final MapSqlParameterSource paramSrc;
    private final StringBuilder sql;
    private final StringBuilder countSql;

    private UserSearchQueryExecutorImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                        UserRowMapper userRowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.userWithRolesExtractor = new UserRowMapper.UserWithRolesExtractor();
        this.sql = new StringBuilder(USER_QUERY_BODY);
        this.countSql = new StringBuilder(USER_COUNT_QUERY_BODY);
        this.paramSrc = new MapSqlParameterSource();
    }

    public static UserSearchQueryExecutor getInstance(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                                      UserRowMapper userRowMapper) {
        return new UserSearchQueryExecutorImpl(namedParameterJdbcTemplate, userRowMapper);
    }

    @Override
    public UserSearchQueryExecutor filter(@Nullable List<SearchParam> filters) {
        if (filters == null) {
            filters = new ArrayList<>();
        }

        List<SearchParam> filtersToApply = new ArrayList<>(filters);
        Set<String> joinSet = new LinkedHashSet<>();
        List<String> whereClauses = new ArrayList<>();
        int paramCounter = 0;

        Iterator<SearchParam> iterator = filtersToApply.iterator();
        while (iterator.hasNext()) {
            SearchParam filter = iterator.next();
            if (filter.getKey() == null) {
                iterator.remove();
                continue;
            }

            switch (filter.getKey()) {
                case USERNAME -> {
                    String paramName = "username_" + paramCounter++;
                    String predicate = filter.getPredicat();
                    List<String> values = filter.getValues();
                    String value = (values != null && !values.isEmpty()) ? values.get(0) : "";

                    if (SearchParam.PREDICAT_ILIKE.equals(predicate)) {
                        whereClauses.add(" u.username ILIKE :" + paramName + " ");
                        paramSrc.addValue(paramName, value);
                    } else if (SearchParam.PREDICAT_LIKE.equals(predicate)) {
                        whereClauses.add(" u.username LIKE :" + paramName + " ");
                        paramSrc.addValue(paramName, value);
                    } else if (SearchParam.PREDICAT_EQ.equals(predicate)) {
                        whereClauses.add(" u.username = :" + paramName + " ");
                        paramSrc.addValue(paramName, value);
                    } else if (SearchParam.PREDICAT_NOT_EQ.equals(predicate)) {
                        whereClauses.add(" u.username != :" + paramName + " ");
                        paramSrc.addValue(paramName, value);
                    } else if (SearchParam.PREDICAT_L_ILIKE.equals(predicate)) {
                        whereClauses.add(" u.username ILIKE :" + paramName + " ");
                        paramSrc.addValue(paramName, "%" + value);
                    } else if (SearchParam.PREDICAT_R_ILIKE.equals(predicate)) {
                        whereClauses.add(" u.username ILIKE :" + paramName + " ");
                        paramSrc.addValue(paramName, value + "%");
                    } else if (SearchParam.PREDICAT_L_LIKE.equals(predicate)) {
                        whereClauses.add(" u.username LIKE :" + paramName + " ");
                        paramSrc.addValue(paramName, "%" + value);
                    } else if (SearchParam.PREDICAT_R_LIKE.equals(predicate)) {
                        whereClauses.add(" u.username LIKE :" + paramName + " ");
                        paramSrc.addValue(paramName, value + "%");
                    } else if (SearchParam.PREDICAT_NOT_ILIKE.equals(predicate)) {
                        whereClauses.add(" u.username NOT ILIKE :" + paramName + " ");
                        paramSrc.addValue(paramName, value);
                    } else if (SearchParam.PREDICAT_NOT_LIKE.equals(predicate)) {
                        whereClauses.add(" u.username NOT LIKE :" + paramName + " ");
                        paramSrc.addValue(paramName, value);
                    } else {
                        // Default: ILIKE
                        whereClauses.add(" u.username ILIKE :" + paramName + " ");
                        paramSrc.addValue(paramName, value);
                    }
                    iterator.remove();
                }
                case ROLE -> {
                    // Ensure role join is present
                    joinSet.add(" LEFT JOIN user_role_entry ur ON u.id = ur.user_id ");

                    String paramName = "role_" + paramCounter++;
                    List<String> values = filter.getValues();
                    String predicate = filter.getPredicat();

                    if (values != null && !values.isEmpty()) {
                        try {
                            Role role = Role.valueOf(values.get(0).toUpperCase());
                            int roleOrdinal = role.ordinal();

                            if (SearchParam.PREDICAT_EQ.equals(predicate) || SearchParam.PREDICAT_TRUE.equals(predicate)) {
                                whereClauses.add(" ur.role = :" + paramName + " ");
                                paramSrc.addValue(paramName, roleOrdinal);
                            } else if (SearchParam.PREDICAT_NOT_EQ.equals(predicate) || SearchParam.PREDICAT_FALSE.equals(predicate)) {
                                whereClauses.add(" ur.role != :" + paramName + " ");
                                paramSrc.addValue(paramName, roleOrdinal);
                            } else if (SearchParam.PREDICAT_IN.equals(predicate)) {
                                List<Integer> ordinals = new ArrayList<>();
                                for (String v : values) {
                                    ordinals.add(Role.valueOf(v.toUpperCase()).ordinal());
                                }
                                whereClauses.add(" ur.role IN (:" + paramName + ") ");
                                paramSrc.addValue(paramName, ordinals);
                            } else if (SearchParam.PREDICAT_NOT_IN.equals(predicate)) {
                                List<Integer> ordinals = new ArrayList<>();
                                for (String v : values) {
                                    ordinals.add(Role.valueOf(v.toUpperCase()).ordinal());
                                }
                                whereClauses.add(" ur.role NOT IN (:" + paramName + ") ");
                                paramSrc.addValue(paramName, ordinals);
                            } else {
                                // Default: equals
                                whereClauses.add(" ur.role = :" + paramName + " ");
                                paramSrc.addValue(paramName, roleOrdinal);
                            }
                        } catch (IllegalArgumentException e) {
                            // Invalid role value — skip this filter
                        }
                    }
                    iterator.remove();
                }
                default -> {
                    // Unknown filter key — skip
                    iterator.remove();
                }
            }
        }

        // Apply joins
        for (String join : joinSet) {
            sql.append(join);
            countSql.append(join);
        }

        // Always join roles for the data query to populate UserEntity.roles
        if (!joinSet.contains(" LEFT JOIN user_role_entry ur ON u.id = ur.user_id ")) {
            sql.append(" LEFT JOIN user_role_entry ur ON u.id = ur.user_id ");
        }

        // Apply WHERE clauses
        if (!whereClauses.isEmpty()) {
            String where = " WHERE " + String.join(" AND ", whereClauses);
            sql.append(where);
            countSql.append(where);
        }

        return this;
    }

    @Override
    public PagingAndExecuteSpec order() {
        sql.append(" ORDER BY u.id, ur.role ");
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
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(
                countSql.toString(), paramSrc, Long.class)).orElse(0L);
    }

    @Override
    public List<UserEntity> executeQuery() {
        return namedParameterJdbcTemplate.query(
                sql.toString(),
                paramSrc,
                userWithRolesExtractor);
    }
}
