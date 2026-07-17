package technology.positivehome.ihome.ai.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link McpToolRegistry}.
 */
class McpToolRegistryTest {

    private McpToolRegistry registry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        registry = new McpToolRegistry(objectMapper);
        // Manually invoke PostConstruct since we're not in Spring context
        registry.registerTools();
    }

    @Test
    void shouldRegisterAllTools() {
        Collection<McpToolDefinition> tools = registry.getAllTools();
        assertTrue(tools.size() >= 14, "Expected at least 14 tools, got " + tools.size());
    }

    @Test
    void shouldIncludeReadOnlyTools() {
        assertTrue(registry.getTool("getSystemSummary").isPresent());
        assertTrue(registry.getTool("getPowerSummary").isPresent());
        assertTrue(registry.getTool("getHeatingSummary").isPresent());
        assertTrue(registry.getTool("getModuleList").isPresent());
        assertTrue(registry.getTool("getModuleData").isPresent());
        assertTrue(registry.getTool("getTempStat").isPresent());
        assertTrue(registry.getTool("getPowerConsumptionStat").isPresent());
    }

    @Test
    void shouldIncludeAdminTools() {
        assertTrue(registry.getTool("updateModuleMode").isPresent());
        assertTrue(registry.getTool("updateModuleOutputState").isPresent());
    }

    @Test
    void adminToolsShouldRequireAdminRole() {
        McpToolDefinition updateMode = registry.getTool("updateModuleMode").orElseThrow();
        assertTrue(updateMode.requiresAdmin());

        McpToolDefinition updateOutput = registry.getTool("updateModuleOutputState").orElseThrow();
        assertTrue(updateOutput.requiresAdmin());
    }

    @Test
    void readOnlyToolsShouldNotRequireAdminRole() {
        McpToolDefinition summary = registry.getTool("getSystemSummary").orElseThrow();
        assertFalse(summary.requiresAdmin());
        assertTrue(summary.isReadOnly());
    }

    @Test
    void adminUserShouldSeeAllTools() {
        List<GrantedAuthority> adminAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_UNDEFINED")
        );

        List<McpToolDefinition> tools = registry.getToolsForRoles(adminAuthorities);
        assertEquals(registry.getAllTools().size(), tools.size(),
                "Admin should see all tools");
    }

    @Test
    void nonAdminUserShouldSeeOnlyReadOnlyTools() {
        List<GrantedAuthority> userAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_UNDEFINED")
        );

        List<McpToolDefinition> tools = registry.getToolsForRoles(userAuthorities);
        assertTrue(tools.size() < registry.getAllTools().size(),
                "Non-admin should see fewer tools than total");
        assertTrue(tools.stream().noneMatch(McpToolDefinition::requiresAdmin),
                "Non-admin should not see admin tools");
    }

    @Test
    void adminCanExecuteAdminTools() {
        List<GrantedAuthority> adminAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        assertTrue(registry.canExecute("updateModuleMode", adminAuthorities));
        assertTrue(registry.canExecute("updateModuleOutputState", adminAuthorities));
        assertTrue(registry.canExecute("getSystemSummary", adminAuthorities));
    }

    @Test
    void nonAdminCannotExecuteAdminTools() {
        List<GrantedAuthority> userAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_UNDEFINED")
        );

        assertFalse(registry.canExecute("updateModuleMode", userAuthorities));
        assertFalse(registry.canExecute("updateModuleOutputState", userAuthorities));
        assertTrue(registry.canExecute("getSystemSummary", userAuthorities));
    }

    @Test
    void unknownToolShouldNotBeExecutable() {
        List<GrantedAuthority> adminAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        assertFalse(registry.canExecute("nonexistentTool", adminAuthorities));
    }

    @Test
    void getToolShouldReturnEmptyForUnknownTool() {
        assertTrue(registry.getTool("nonexistentTool").isEmpty());
    }
}
