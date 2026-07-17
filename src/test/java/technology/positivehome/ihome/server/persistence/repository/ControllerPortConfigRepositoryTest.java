package technology.positivehome.ihome.server.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import technology.positivehome.ihome.model.constant.IHomePortType;
import technology.positivehome.ihome.server.persistence.mapper.ControllerPortConfigEntryRowMapper;
import technology.positivehome.ihome.server.persistence.model.ControllerPortConfigEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerPortConfigRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Mock
    private ControllerPortConfigEntryRowMapper rowMapper;

    private ControllerPortConfigRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ControllerPortConfigRepositoryImpl(namedParameterJdbcTemplate, rowMapper);
    }

    @Test
    void findByControllerId_shouldReturnPorts() {
        // Arrange
        long controllerId = 1L;
        List<ControllerPortConfigEntity> expected = List.of(
                new ControllerPortConfigEntity(1L, controllerId, IHomePortType.RELAY, 1, "Port 1"),
                new ControllerPortConfigEntity(2L, controllerId, IHomePortType.DIMMER, 2, "Port 2")
        );
        when(namedParameterJdbcTemplate.query(
                anyString(),
                eq(Map.of("controller_id", controllerId)),
                eq(rowMapper)))
                .thenReturn(expected);

        // Act
        List<ControllerPortConfigEntity> result = repository.findByControllerId(controllerId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Port 1", result.get(0).description());
        assertEquals(IHomePortType.RELAY, result.get(0).type());
    }

    @Test
    void findByControllerId_shouldReturnEmpty_whenNoPorts() {
        // Arrange
        long controllerId = 999L;
        when(namedParameterJdbcTemplate.query(
                anyString(),
                eq(Map.of("controller_id", controllerId)),
                eq(rowMapper)))
                .thenReturn(List.of());

        // Act
        List<ControllerPortConfigEntity> result = repository.findByControllerId(controllerId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
