package technology.positivehome.ihome.server.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import technology.positivehome.ihome.model.constant.ControllerType;
import technology.positivehome.ihome.server.persistence.mapper.ControllerConfigEntryRowMapper;
import technology.positivehome.ihome.server.persistence.model.ControllerConfigEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerConfigRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Mock
    private ControllerConfigEntryRowMapper rowMapper;

    private ControllerConfigRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ControllerConfigRepositoryImpl(namedParameterJdbcTemplate, rowMapper);
    }

    @Test
    void getById_shouldReturnEntity() {
        // Arrange
        long id = 1L;
        ControllerConfigEntity expected = new ControllerConfigEntity(id, ControllerType.MEGAD, "Test", "192.168.1.1", 8080);
        when(namedParameterJdbcTemplate.queryForObject(anyString(), eq(Map.of("id", id)), eq(rowMapper)))
                .thenReturn(expected);

        // Act
        ControllerConfigEntity result = repository.getById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Test", result.name());
        assertEquals(ControllerType.MEGAD, result.type());
    }

    @Test
    void findAll_shouldReturnAllEntities() {
        // Arrange
        List<ControllerConfigEntity> expected = List.of(
                new ControllerConfigEntity(1L, ControllerType.MEGAD, "Ctrl1", "192.168.1.1", 8080),
                new ControllerConfigEntity(2L, ControllerType.USR404, "Ctrl2", "192.168.1.2", 8081)
        );
        when(namedParameterJdbcTemplate.query(anyString(), eq(rowMapper)))
                .thenReturn(expected);

        // Act
        List<ControllerConfigEntity> result = repository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Ctrl1", result.get(0).name());
        assertEquals("Ctrl2", result.get(1).name());
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        // Arrange
        when(namedParameterJdbcTemplate.queryForObject(anyString(), eq(Map.of("id", 999L)), eq(rowMapper)))
                .thenThrow(new org.springframework.dao.EmptyResultDataAccessException(1));

        // Act & Assert
        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class,
                () -> repository.getById(999L));
    }
}
