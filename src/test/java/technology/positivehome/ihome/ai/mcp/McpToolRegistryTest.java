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
        assertEquals(15, tools.size(), "Expected exactly 15 tools (5 public-read, 8 restricted-read, 2 write)");
    }

    @Test
    void shouldIncludePublicReadTools() {
        // Outdoor temperature/humidity, pressure, and external power supply (guest controller data)
        assertTrue(registry.getTool("getTempStat").isPresent());
        assertTrue(registry.getTool("getPressureStat").isPresent());
        assertTrue(registry.getTool("getPowerSummary").isPresent());
        assertTrue(registry.getTool("getPowerConsumptionStat").isPresent());
        assertTrue(registry.getTool("getPowerVoltageStat").isPresent());
    }

    @Test
    void shouldIncludeRestrictedReadTools() {
        assertTrue(registry.getTool("getSystemSummary").isPresent());
        assertTrue(registry.getTool("getHeatingSummary").isPresent());
        assertTrue(registry.getTool("getLuminosityStat").isPresent());
        assertTrue(registry.getTool("getSystemStat").isPresent());
        assertTrue(registry.getTool("getModuleList").isPresent());
        assertTrue(registry.getTool("getModuleData").isPresent());
        assertTrue(registry.getTool("getModuleListByGroup").isPresent());
        assertTrue(registry.getTool("getBoilerTempStat").isPresent());
    }

    @Test
    void shouldIncludeWriteTools() {
        assertTrue(registry.getTool("updateModuleMode").isPresent());
        assertTrue(registry.getTool("updateModuleOutputState").isPresent());
    }

    @Test
    void writeToolsShouldRequireModuleWritePermission() {
        McpToolDefinition updateMode = registry.getTool("updateModuleMode").orElseThrow();
        assertTrue(updateMode.requiresModuleWritePermission(),
                "updateModuleMode should require MODULE WRITE permission");

        McpToolDefinition updateOutput = registry.getTool("updateModuleOutputState").orElseThrow();
        assertTrue(updateOutput.requiresModuleWritePermission(),
                "updateModuleOutputState should require MODULE WRITE permission");
    }

    @Test
    void publicReadToolsShouldBePublicRead() {
        McpToolDefinition pressure = registry.getTool("getPressureStat").orElseThrow();
        assertFalse(pressure.requiresModuleWritePermission());
        assertTrue(pressure.isPublicRead());
        assertFalse(pressure.isRestrictedRead());
        assertTrue(pressure.isReadOnly());
    }

    @Test
    void restrictedReadToolsShouldBeRestricted() {
        McpToolDefinition moduleList = registry.getTool("getModuleList").orElseThrow();
        assertFalse(moduleList.requiresModuleWritePermission());
        assertFalse(moduleList.isPublicRead());
        assertTrue(moduleList.isRestrictedRead());
        assertTrue(moduleList.isReadOnly());
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
    void supervisorUserShouldSeeAllTools() {
        List<GrantedAuthority> supervisorAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_SUPERVISOR")
        );

        List<McpToolDefinition> tools = registry.getToolsForRoles(supervisorAuthorities);
        assertEquals(registry.getAllTools().size(), tools.size(),
                "Supervisor should see all tools (public-read + restricted-read + write)");
    }

    @Test
    void nonAdminUserShouldSeeOnlyReadOnlyTools() {
        List<GrantedAuthority> userAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_UNDEFINED")
        );

        List<McpToolDefinition> tools = registry.getToolsForRoles(userAuthorities);
        assertTrue(tools.size() < registry.getAllTools().size(),
                "Non-admin should see fewer tools than total");
        assertTrue(tools.stream().noneMatch(McpToolDefinition::requiresModuleWritePermission),
                "Non-admin should not see module-write tools");
    }

    @Test
    void authorizedGuestShouldSeeOnlyPublicReadTools() {
        List<GrantedAuthority> guestAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_AUTHORIZED_GUEST")
        );

        List<McpToolDefinition> tools = registry.getToolsForRoles(guestAuthorities);

        // Guest should see ONLY public-read tools (5 tools), not restricted-read (8) or write (2)
        assertEquals(5, tools.size(),
                "AUTHORIZED_GUEST should see only 5 public-read tools");
        assertTrue(tools.stream().noneMatch(McpToolDefinition::requiresModuleWritePermission),
                "AUTHORIZED_GUEST should not see module-write tools");
        assertTrue(tools.stream().noneMatch(McpToolDefinition::isRestrictedRead),
                "AUTHORIZED_GUEST should not see restricted-read tools");
        assertTrue(tools.stream().allMatch(McpToolDefinition::isPublicRead),
                "AUTHORIZED_GUEST should only see public-read tools");
        assertTrue(tools.stream().anyMatch(t -> t.name().equals("getPressureStat")),
                "AUTHORIZED_GUEST should see getPressureStat");
        assertTrue(tools.stream().anyMatch(t -> t.name().equals("getTempStat")),
                "AUTHORIZED_GUEST should see getTempStat");
        assertTrue(tools.stream().anyMatch(t -> t.name().equals("getPowerSummary")),
                "AUTHORIZED_GUEST should see getPowerSummary");
        assertTrue(tools.stream().anyMatch(t -> t.name().equals("getPowerConsumptionStat")),
                "AUTHORIZED_GUEST should see getPowerConsumptionStat");
        assertTrue(tools.stream().anyMatch(t -> t.name().equals("getPowerVoltageStat")),
                "AUTHORIZED_GUEST should see getPowerVoltageStat");
    }

    @Test
    void adminCanExecuteAllTools() {
        List<GrantedAuthority> adminAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        // Can execute write tools
        assertTrue(registry.canExecute("updateModuleMode", adminAuthorities));
        assertTrue(registry.canExecute("updateModuleOutputState", adminAuthorities));

        // Can execute restricted-read tools (everything not public-read or write)
        assertTrue(registry.canExecute("getSystemSummary", adminAuthorities));
        assertTrue(registry.canExecute("getHeatingSummary", adminAuthorities));

        // Can execute public-read tools
        assertTrue(registry.canExecute("getTempStat", adminAuthorities));
        assertTrue(registry.canExecute("getPressureStat", adminAuthorities));
        assertTrue(registry.canExecute("getPowerSummary", adminAuthorities));
        assertTrue(registry.canExecute("getPowerVoltageStat", adminAuthorities));

        // Can execute module tools
        assertTrue(registry.canExecute("getModuleList", adminAuthorities));
        assertTrue(registry.canExecute("getModuleData", adminAuthorities));
    }

    @Test
    void nonAdminCannotExecuteWriteTools() {
        List<GrantedAuthority> userAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_UNDEFINED")
        );

        assertFalse(registry.canExecute("updateModuleMode", userAuthorities));
        assertFalse(registry.canExecute("updateModuleOutputState", userAuthorities));
        assertTrue(registry.canExecute("getSystemSummary", userAuthorities));
    }

    @Test
    void authorizedGuestCannotExecuteRestrictedOrWriteTools() {
        List<GrantedAuthority> guestAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_AUTHORIZED_GUEST")
        );

        // Write tools — denied
        assertFalse(registry.canExecute("updateModuleMode", guestAuthorities));
        assertFalse(registry.canExecute("updateModuleOutputState", guestAuthorities));

        // Restricted-read tools — denied
        assertFalse(registry.canExecute("getSystemSummary", guestAuthorities));
        assertFalse(registry.canExecute("getHeatingSummary", guestAuthorities));
        assertFalse(registry.canExecute("getLuminosityStat", guestAuthorities));
        assertFalse(registry.canExecute("getSystemStat", guestAuthorities));
        assertFalse(registry.canExecute("getModuleList", guestAuthorities));
        assertFalse(registry.canExecute("getModuleData", guestAuthorities));
        assertFalse(registry.canExecute("getModuleListByGroup", guestAuthorities));
        assertFalse(registry.canExecute("getBoilerTempStat", guestAuthorities));

        // Public-read tools (outdoor temp/humidity, pressure, external power supply) — allowed
        assertTrue(registry.canExecute("getTempStat", guestAuthorities));
        assertTrue(registry.canExecute("getPressureStat", guestAuthorities));
        assertTrue(registry.canExecute("getPowerSummary", guestAuthorities));
        assertTrue(registry.canExecute("getPowerConsumptionStat", guestAuthorities));
        assertTrue(registry.canExecute("getPowerVoltageStat", guestAuthorities));
    }

    @Test
    void authorizedGuestWithUndefinedShouldStillBeRestricted() {
        // Mixed guest+undefined should still be treated as guest-only
        List<GrantedAuthority> mixedAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_AUTHORIZED_GUEST"),
                new SimpleGrantedAuthority("ROLE_UNDEFINED")
        );

        assertFalse(registry.canExecute("getModuleList", mixedAuthorities));
        assertFalse(registry.canExecute("updateModuleMode", mixedAuthorities));
        assertTrue(registry.canExecute("getPressureStat", mixedAuthorities));
        assertFalse(registry.canExecute("getSystemSummary", mixedAuthorities));
    }

    @Test
    void supervisorCanExecuteRestrictedAndWriteTools() {
        List<GrantedAuthority> supervisorAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_SUPERVISOR")
        );

        // Write tools — allowed
        assertTrue(registry.canExecute("updateModuleMode", supervisorAuthorities));
        assertTrue(registry.canExecute("updateModuleOutputState", supervisorAuthorities));

        // Restricted-read tools — allowed
        assertTrue(registry.canExecute("getSystemSummary", supervisorAuthorities));
        assertTrue(registry.canExecute("getModuleList", supervisorAuthorities));
        assertTrue(registry.canExecute("getModuleData", supervisorAuthorities));
        assertTrue(registry.canExecute("getBoilerTempStat", supervisorAuthorities));

        // Public-read tools — allowed
        assertTrue(registry.canExecute("getTempStat", supervisorAuthorities));
        assertTrue(registry.canExecute("getPressureStat", supervisorAuthorities));
        assertTrue(registry.canExecute("getPowerSummary", supervisorAuthorities));
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

    @Test
    void getToolShouldReturnCorrectAccessTypes() {
        assertEquals(McpToolAccessType.PUBLIC_READ, registry.getTool("getPressureStat").get().accessType());
        assertEquals(McpToolAccessType.RESTRICTED_READ, registry.getTool("getSystemSummary").get().accessType());
        assertEquals(McpToolAccessType.RESTRICTED_READ, registry.getTool("getModuleList").get().accessType());
        assertEquals(McpToolAccessType.WRITE, registry.getTool("updateModuleMode").get().accessType());
    }
}
