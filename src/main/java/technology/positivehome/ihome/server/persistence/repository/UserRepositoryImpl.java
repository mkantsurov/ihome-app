package technology.positivehome.ihome.server.persistence.repository;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.server.model.EntityComparisonResult;
import technology.positivehome.ihome.server.model.SearchParam;
import technology.positivehome.ihome.server.model.SessionInfo;
import technology.positivehome.ihome.server.persistence.mapper.UserRowMapper;
import technology.positivehome.ihome.server.persistence.model.UserEntity;
import technology.positivehome.ihome.server.persistence.repository.queryexec.UserSearchQueryExecutorImpl;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final String SELECT_USER_BY_ID = """
            SELECT u.id, u.username, u.password, ur.role
            FROM user_entry u
            LEFT JOIN user_role_entry ur ON ur.user_id = u.id
            WHERE u.id = :id
            ORDER BY ur.role
            """;

    private static final String SELECT_USER_BY_USERNAME = """
            SELECT u.id, u.username, u.password, ur.role
            FROM user_entry u
            LEFT JOIN user_role_entry ur ON ur.user_id = u.id
            WHERE u.username = :username
            ORDER BY ur.role
            """;

    private static final String SELECT_ALL_USERS = """
            SELECT u.id, u.username, u.password, ur.role
            FROM user_entry u
            LEFT JOIN user_role_entry ur ON ur.user_id = u.id
            ORDER BY u.id, ur.role
            """;

    private static final String SELECT_USER_BY_USERNAME_PATTERN = """
            SELECT u.id, u.username, u.password, ur.role
            FROM user_entry u
            LEFT JOIN user_role_entry ur ON ur.user_id = u.id
            WHERE u.username ILIKE :pattern
            ORDER BY u.id, ur.role
            OFFSET :offset LIMIT :limit
            """;

    private static final String COUNT_USER_BY_USERNAME_PATTERN = """
            SELECT COUNT(*) FROM user_entry WHERE username ILIKE :pattern
            """;

    private static final String COUNT_BY_USERNAME = """
            SELECT COUNT(*) FROM user_entry WHERE username = :username
            """;

    private static final String INSERT_USER = """
            INSERT INTO user_entry (username, password)
            VALUES (:username, :password)
            """;

    private static final String INSERT_USER_ROLE = """
            INSERT INTO user_role_entry (user_id, role)
            VALUES (:user_id, :role)
            """;

    private static final String UPDATE_USER = """
            UPDATE user_entry SET username = :username, password = :password
            WHERE id = :id
            """;

    private static final String DELETE_USER_ROLES = """
            DELETE FROM user_role_entry WHERE user_id = :user_id
            """;

    private static final String DELETE_USER = """
            DELETE FROM user_entry WHERE id = :id
            """;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserRowMapper userRowMapper;
    private final UserRowMapper.UserWithRolesExtractor userWithRolesExtractor;

    public UserRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              UserRowMapper userRowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.userRowMapper = userRowMapper;
        this.userWithRolesExtractor = new UserRowMapper.UserWithRolesExtractor();
    }

    @Override
    @Nonnull
    public UserEntity getById(@Nonnull Long id) {
        List<UserEntity> users = namedParameterJdbcTemplate.query(
                SELECT_USER_BY_ID,
                Map.of("id", id),
                userWithRolesExtractor);
        if (users.isEmpty()) {
            throw new org.springframework.dao.EmptyResultDataAccessException(
                    "UserEntity with id " + id + " not found", 1);
        }
        return users.get(0);
    }

    @Override
    @Nonnull
    public Optional<UserEntity> findByUsername(@Nonnull String username) {
        List<UserEntity> users = namedParameterJdbcTemplate.query(
                SELECT_USER_BY_USERNAME,
                Map.of("username", username),
                userWithRolesExtractor);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    @Nonnull
    public Optional<UserEntity> findById(long userId) {
        List<UserEntity> users = namedParameterJdbcTemplate.query(
                SELECT_USER_BY_ID,
                Map.of("id", userId),
                userWithRolesExtractor);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    @Override
    public boolean existsByUsername(@Nonnull String username) {
        Integer count = namedParameterJdbcTemplate.queryForObject(
                COUNT_BY_USERNAME,
                Map.of("username", username),
                Integer.class);
        return count != null && count > 0;
    }

    @Override
    @Nonnull
    public List<UserEntity> findAll() {
        return namedParameterJdbcTemplate.query(
                SELECT_ALL_USERS,
                userWithRolesExtractor);
    }

    @Override
    @Nonnull
    public List<UserEntity> findByUsernamePattern(@Nonnull String usernamePattern, int page, int size) {
        int offset = page * size;
        return namedParameterJdbcTemplate.query(
                SELECT_USER_BY_USERNAME_PATTERN,
                Map.of("pattern", usernamePattern, "offset", offset, "limit", size),
                userWithRolesExtractor);
    }

    @Override
    public long countByUsernamePattern(@Nonnull String usernamePattern) {
        Long count = namedParameterJdbcTemplate.queryForObject(
                COUNT_USER_BY_USERNAME_PATTERN,
                Map.of("pattern", usernamePattern),
                Long.class);
        return count != null ? count : 0L;
    }

    @Override
    @Nonnull
    public List<UserEntity> searchUsers(@Nullable List<SearchParam> filters, @Nullable Integer page, @Nullable Integer size) {
        return UserSearchQueryExecutorImpl.getInstance(namedParameterJdbcTemplate, userRowMapper)
                .filter(filters)
                .order()
                .page(page, size)
                .executeQuery();
    }

    @Override
    public long countUsers(@Nullable List<SearchParam> filters) {
        return UserSearchQueryExecutorImpl.getInstance(namedParameterJdbcTemplate, userRowMapper)
                .filter(filters)
                .getCount();
    }

    @Override
    @Nonnull
    public Long create(SessionInfo sessionInfo, @Nullable Long rootEntityId, @Nonnull UserEntity entity) {
        // TODO: Integrate with audit logging (LogRepository/audit_log_entry)
        // For now, delegates to the non-audited create
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource userParams = new MapSqlParameterSource()
                .addValue("username", entity.username())
                .addValue("password", entity.password());

        namedParameterJdbcTemplate.update(INSERT_USER, userParams, keyHolder, new String[]{"id"});

        long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        insertRoles(userId, entity.roles());

        return userId;
    }

    @Override
    public EntityComparisonResult update(SessionInfo sessionInfo, @Nullable Long rootEntityId, @Nonnull UserEntity entity) {
        // TODO: Integrate with audit logging (LogRepository/audit_log_entry)
        // For now, delegates to the non-audited update
        MapSqlParameterSource userParams = new MapSqlParameterSource()
                .addValue("id", entity.id())
                .addValue("username", entity.username())
                .addValue("password", entity.password());

        namedParameterJdbcTemplate.update(UPDATE_USER, userParams);

        // Replace roles: delete existing, insert new
        namedParameterJdbcTemplate.update(DELETE_USER_ROLES, Map.of("user_id", entity.id()));
        insertRoles(entity.id(), entity.roles());
        return new EntityComparisonResult();
    }

    @Override
    public void remove(SessionInfo sessionInfo, @Nullable Long rootEntityId, @Nonnull Long id) {
        // TODO: Integrate with audit logging (LogRepository/audit_log_entry)
        // For now, delegates to the non-audited remove
        namedParameterJdbcTemplate.update(DELETE_USER_ROLES, Map.of("user_id", id));
        namedParameterJdbcTemplate.update(DELETE_USER, Map.of("id", id));
    }

    private void insertRoles(long userId, List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return;
        }
        MapSqlParameterSource[] batchArgs = roles.stream()
                .map(role -> new MapSqlParameterSource()
                        .addValue("user_id", userId)
                        .addValue("role", role.ordinal()))
                .toArray(MapSqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(INSERT_USER_ROLE, batchArgs);
    }
}
