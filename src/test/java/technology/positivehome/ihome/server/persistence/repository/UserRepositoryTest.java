package technology.positivehome.ihome.server.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.server.persistence.mapper.UserRowMapper;
import technology.positivehome.ihome.server.persistence.model.UserEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Mock
    private UserRowMapper userRowMapper;

    @Captor
    private ArgumentCaptor<MapSqlParameterSource> parameterSourceCaptor;
    @Captor
    private ArgumentCaptor<Map<String, Object>> mapCaptor;

    private UserRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new UserRepositoryImpl(namedParameterJdbcTemplate, userRowMapper);
    }

    @Test
    void getById_shouldReturnUserWithRoles() {
        // Arrange
        long id = 1L;
        List<UserEntity> expectedUsers = List.of(
                new UserEntity(id, "admin", "hashed_pwd", List.of(Role.ADMIN))
        );

        when(namedParameterJdbcTemplate.query(
                anyString(),
                eq(Map.of("id", id)),
                any(ResultSetExtractor.class)))
                .thenReturn(expectedUsers);

        // Act
        UserEntity result = repository.getById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.id().longValue());
        assertEquals("admin", result.username());
        assertTrue(result.roles().contains(Role.ADMIN));
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        // Arrange
        long id = 999L;
        when(namedParameterJdbcTemplate.query(
                anyString(),
                eq(Map.of("id", id)),
                any(ResultSetExtractor.class)))
                .thenReturn(List.of());

        // Act & Assert
        assertThrows(EmptyResultDataAccessException.class, () -> repository.getById(id));
    }

    @Test
    void findByUsername_shouldReturnUser() {
        // Arrange
        String username = "admin";
        List<UserEntity> expectedUsers = List.of(
                new UserEntity(1L, username, "hashed_pwd", List.of(Role.ADMIN))
        );

        when(namedParameterJdbcTemplate.query(
                anyString(),
                eq(Map.of("username", username)),
                any(ResultSetExtractor.class)))
                .thenReturn(expectedUsers);

        // Act
        Optional<UserEntity> result = repository.findByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(username, result.get().username());
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenNotFound() {
        // Arrange
        String username = "nonexistent";
        when(namedParameterJdbcTemplate.query(
                anyString(),
                eq(Map.of("username", username)),
                any(ResultSetExtractor.class)))
                .thenReturn(List.of());

        // Act
        Optional<UserEntity> result = repository.findByUsername(username);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenExists() {
        // Arrange
        String username = "admin";
        when(namedParameterJdbcTemplate.queryForObject(
                anyString(),
                eq(Map.of("username", username)),
                eq(Integer.class)))
                .thenReturn(1);

        // Act
        boolean result = repository.existsByUsername(username);

        // Assert
        assertTrue(result);
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenNotExists() {
        // Arrange
        String username = "nonexistent";
        when(namedParameterJdbcTemplate.queryForObject(
                anyString(),
                eq(Map.of("username", username)),
                eq(Integer.class)))
                .thenReturn(0);

        // Act
        boolean result = repository.existsByUsername(username);

        // Assert
        assertFalse(result);
    }

    @Test
    void create_shouldPersistUserAndRoles() {
        // Arrange
        UserEntity entity = new UserEntity(null, "newuser", "hashed_pwd", List.of(Role.ADMIN));

        when(namedParameterJdbcTemplate.update(
                anyString(),
                any(MapSqlParameterSource.class),
                any(GeneratedKeyHolder.class),
                any(String[].class)))
                .thenAnswer(invocation -> {
                    GeneratedKeyHolder keyHolder = invocation.getArgument(2);
                    // Simulate key generation
                    return 1;
                });

        when(namedParameterJdbcTemplate.batchUpdate(
                anyString(),
                any(MapSqlParameterSource[].class)))
                .thenReturn(new int[]{1});

        // Act
        Long result = repository.create(entity);

        // Assert
        assertNotNull(result);
        verify(namedParameterJdbcTemplate).batchUpdate(
                anyString(),
                any(MapSqlParameterSource[].class));
    }

    @Test
    void create_shouldPersistUserWithoutRoles() {
        // Arrange
        UserEntity entity = new UserEntity(null, "newuser", "hashed_pwd", List.of());

        when(namedParameterJdbcTemplate.update(
                anyString(),
                any(MapSqlParameterSource.class),
                any(GeneratedKeyHolder.class),
                any(String[].class)))
                .thenAnswer(invocation -> {
                    GeneratedKeyHolder keyHolder = invocation.getArgument(2);
                    return 1;
                });

        // Act
        Long result = repository.create(entity);

        // Assert
        assertNotNull(result);
        // Verify batchUpdate was NOT called (no roles)
        verify(namedParameterJdbcTemplate, never()).batchUpdate(
                anyString(),
                any(MapSqlParameterSource[].class));
    }

    @Test
    void update_shouldModifyUserAndReplaceRoles() {
        // Arrange
        UserEntity entity = new UserEntity(1L, "updated_user", "new_hashed_pwd", List.of(Role.ADMIN));

        when(namedParameterJdbcTemplate.update(
                anyString(),
                any(MapSqlParameterSource.class)))
                .thenReturn(1);

        when(namedParameterJdbcTemplate.update(
                eq("DELETE FROM user_role_entry WHERE user_id = :user_id"),
                any(Map.class)))
                .thenReturn(1);

        when(namedParameterJdbcTemplate.batchUpdate(
                anyString(),
                any(MapSqlParameterSource[].class)))
                .thenReturn(new int[]{1});

        // Act
        repository.update(entity);

        // Assert
        verify(namedParameterJdbcTemplate).update(
                eq("DELETE FROM user_role_entry WHERE user_id = :user_id"),
                any(Map.class));
        verify(namedParameterJdbcTemplate).batchUpdate(
                anyString(),
                any(MapSqlParameterSource[].class));
    }

    @Test
    void remove_shouldDeleteUserAndRoles() {
        // Arrange
        long id = 1L;

        when(namedParameterJdbcTemplate.update(
                eq("DELETE FROM user_role_entry WHERE user_id = :user_id"),
                any(Map.class)))
                .thenReturn(1);

        when(namedParameterJdbcTemplate.update(
                eq("DELETE FROM user_entry WHERE id = :id"),
                any(Map.class)))
                .thenReturn(1);

        // Act
        repository.remove(id);

        // Assert
        verify(namedParameterJdbcTemplate).update(
                eq("DELETE FROM user_role_entry WHERE user_id = :user_id"),
                eq(Map.of("user_id", id)));
        verify(namedParameterJdbcTemplate).update(
                eq("DELETE FROM user_entry WHERE id = :id"),
                eq(Map.of("id", id)));
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        // Arrange
        List<UserEntity> expectedUsers = List.of(
                new UserEntity(1L, "admin", "hashed_pwd1", List.of(Role.ADMIN)),
                new UserEntity(2L, "user", "hashed_pwd2", List.of())
        );

        when(namedParameterJdbcTemplate.query(
                anyString(),
                any(ResultSetExtractor.class)))
                .thenReturn(expectedUsers);

        // Act
        List<UserEntity> result = repository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("admin", result.get(0).username());
    }

    @Test
    void auditableCreate_shouldDelegateToCreate() {
        // Arrange
        UserEntity entity = new UserEntity(null, "audit_user", "hashed_pwd", List.of(Role.ADMIN));

        when(namedParameterJdbcTemplate.update(
                anyString(),
                any(MapSqlParameterSource.class),
                any(GeneratedKeyHolder.class),
                any(String[].class)))
                .thenAnswer(invocation -> {
                    GeneratedKeyHolder keyHolder = invocation.getArgument(2);
                    return 1;
                });

        when(namedParameterJdbcTemplate.batchUpdate(
                anyString(),
                any(MapSqlParameterSource[].class)))
                .thenReturn(new int[]{1});

        // Act
        Long result = repository.create(null, null, entity);

        // Assert
        assertNotNull(result);
    }

    @Test
    void auditableRemove_shouldDelegateToRemove() {
        // Arrange
        long id = 1L;
        when(namedParameterJdbcTemplate.update(
                anyString(),
                any(Map.class)))
                .thenReturn(1);

        // Act
        repository.remove(null, null, id);

        // Assert
        verify(namedParameterJdbcTemplate).update(
                eq("DELETE FROM user_role_entry WHERE user_id = :user_id"),
                eq(Map.of("user_id", id)));
        verify(namedParameterJdbcTemplate).update(
                eq("DELETE FROM user_entry WHERE id = :id"),
                eq(Map.of("id", id)));
    }
}
