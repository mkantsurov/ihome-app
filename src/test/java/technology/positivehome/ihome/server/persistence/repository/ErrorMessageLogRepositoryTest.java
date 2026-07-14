package technology.positivehome.ihome.server.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import technology.positivehome.ihome.model.constant.ErrorEventType;
import technology.positivehome.ihome.server.persistence.mapper.ErrorMessageLogRowMapper;
import technology.positivehome.ihome.server.persistence.model.ErrorMessageLogEntity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorMessageLogRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Mock
    private ErrorMessageLogRowMapper rowMapper;

    private ErrorMessageLogRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ErrorMessageLogRepositoryImpl(namedParameterJdbcTemplate, rowMapper);
    }

    @Test
    void getById_shouldReturnEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        ErrorMessageLogEntity expected = new ErrorMessageLogEntity(
                id, LocalDateTime.now(), ErrorEventType.GENERIC_ERROR, "Test error");
        when(namedParameterJdbcTemplate.queryForObject(anyString(), eq(Map.of("id", id)), eq(rowMapper)))
                .thenReturn(expected);

        // Act
        ErrorMessageLogEntity result = repository.getById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Test error", result.message());
        assertEquals(ErrorEventType.GENERIC_ERROR, result.type());
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(namedParameterJdbcTemplate.queryForObject(anyString(), eq(Map.of("id", id)), eq(rowMapper)))
                .thenThrow(new EmptyResultDataAccessException(1));

        // Act & Assert
        assertThrows(EmptyResultDataAccessException.class, () -> repository.getById(id));
    }

    @Test
    void create_shouldReturnNewId() {
        // Arrange
        ErrorMessageLogEntity entity = new ErrorMessageLogEntity(
                null, LocalDateTime.now(), ErrorEventType.GENERIC_ERROR, "New error");
        when(namedParameterJdbcTemplate.update(anyString(), any(MapSqlParameterSource.class)))
                .thenReturn(1);

        // Act
        UUID result = repository.create(entity);

        // Assert
        assertNotNull(result);
    }

    @Test
    void update_shouldThrowNotImplemented() {
        // Arrange
        ErrorMessageLogEntity entity = new ErrorMessageLogEntity(
                UUID.randomUUID(), LocalDateTime.now(), ErrorEventType.GENERIC_ERROR, "Test");

        // Act & Assert
        assertThrows(org.apache.commons.lang3.NotImplementedException.class,
                () -> repository.update(entity));
    }

    @Test
    void remove_shouldThrowNotImplemented() {
        // Act & Assert
        assertThrows(org.apache.commons.lang3.NotImplementedException.class,
                () -> repository.remove(UUID.randomUUID()));
    }
}
