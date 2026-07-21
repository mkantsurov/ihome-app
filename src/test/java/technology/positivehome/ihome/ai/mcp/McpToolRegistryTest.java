package technology.positivehome.ihome.ai.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.security.service.PermissionService;
import technology.positivehome.ihome.server.persistence.ModuleConfigRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link McpToolRegistry}.
 */
@ExtendWith(MockitoExtension.class)
class McpToolRegistryTest {

    @Mock
    private ModuleConfigRepository moduleConfigRepository;

    private McpToolRegistry registry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Stub to simulate that supervisor-controllable modules exist in the system
        when(moduleConfigRepository.hasAnyModuleWithAssignments(anySet())).thenReturn(true);

        PermissionService permissionService = new PermissionService(moduleConfigRepository);
        registry = new McpToolRegistry(objectMapper, permissionService);
        registry.registerTools();
    }

    private static Authentication auth(Role... roles) {
        List<GrantedAuthority> authorities = Arrays.stream(roles)
                .map(r -> new SimpleGrantedAuthority(r.authority()))
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken("user", null, authorities);
    }

    @Test
    void shouldRegisterAllTools() {
        Collection<McpToolDefinition> tools = registry.getAllTools();
        assertEquals(16, tools.size(), "Expected exactly 16 tools (4 public-read, 10 restricted-read, 1 write, 1 admin-only)");
    }

    @Test
    void shouldIncludePublicReadTools() {
        // Outdoor temperature/humidity, pressure, external power voltage, and external power summary (guest controller data)
        assertTrue(registry.getTool("getTempStat").isPresent());
        assertTrue(registry.getTool("getPressureStat").isPresent());
        assertTrue(registry.getTool("getPowerSummary").isPresent());
        assertTrue(registry.getTool("getPowerVoltageExtStat").isPresent());
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
        assertTrue(registry.getTool("getPowerConsumptionStat").isPresent(),
                "getPowerConsumptionStat includes internal consumption data, should be restricted-read");
        assertTrue(registry.getTool("getPowerVoltageStat").isPresent(),
                "getPowerVoltageStat includes internal voltage data, should be restricted-read");
    }

    @Test
    void shouldIncludeWriteAndAdminTools() {
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
        Authentication admin = new UsernamePasswordAuthenticationToken("admin", null,
                List.of(new SimpleGrantedAuthority(Role.ADMIN.authority()),
                        new SimpleGrantedAuthority(Role.UNDEFINED.authority())));

        List<McpToolDefinition> tools = registry.getToolsForRoles(admin);
        assertEquals(registry.getAllTools().size(), tools.size(),
                "Admin should see all tools");
    }

    @Test
    void supervisorUserShouldSeeAllToolsExceptAdminOnly() {
        Authentication supervisor = new UsernamePasswordAuthenticationToken("supervisor", null,
                List.of(new SimpleGrantedAuthority(Role.SUPERVISOR.authority())));

        List<McpToolDefinition> tools = registry.getToolsForRoles(supervisor);
        // Supervisor sees: 4 public-read + 10 restricted-read + 1 write = 15 (not updateModuleMode which is ADMIN_ONLY)
        assertEquals(15, tools.size(),
                "Supervisor should see 15 tools (public-read + restricted-read + write), excluding admin-only");
        assertTrue(tools.stream().noneMatch(t -> t.name().equals("updateModuleMode")),
                "Supervisor should not see ADMIN_ONLY tool updateModuleMode");
    }

    @Test
    void nonAdminUserShouldSeeOnlyReadOnlyTools() {
        Authentication user = new UsernamePasswordAuthenticationToken("user", null,
                List.of(new SimpleGrantedAuthority(Role.UNDEFINED.authority())));

        List<McpToolDefinition> tools = registry.getToolsForRoles(user);
        assertTrue(tools.size() < registry.getAllTools().size(),
                "Non-admin should see fewer tools than total");
        assertTrue(tools.stream().noneMatch(McpToolDefinition::requiresModuleWritePermission),
                "Non-admin should not see module-write tools");
    }

    @Test
    void authorizedGuestShouldSeeOnlyPublicReadTools() {
        Authentication guest = new UsernamePasswordAuthenticationToken("guest", null,
                List.of(new SimpleGrantedAuthority(Role.AUTHORIZED_GUEST.authority())));

        List<McpToolDefinition> tools = registry.getToolsForRoles(guest);

        // Guest should see ONLY public-read tools (4 tools), not restricted-read (10) or write (2)
        assertEquals(4, tools.size(),
                "AUTHORIZED_GUEST should see only 4 public-read tools");
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
        assertTrue(tools.stream().anyMatch(t -> t.name().equals("getPowerVoltageExtStat")),
                "AUTHORIZED_GUEST should see getPowerVoltageExtStat (external voltage only, matches guest controller)");
        assertTrue(tools.stream().noneMatch(t -> t.name().equals("getPowerConsumptionStat")),
                "AUTHORIZED_GUEST should NOT see getPowerConsumptionStat (includes internal data)");
        assertTrue(tools.stream().noneMatch(t -> t.name().equals("getPowerVoltageStat")),
                "AUTHORIZED_GUEST should NOT see getPowerVoltageStat (includes internal data)");
    }

    @Test
    void adminCanExecuteAllTools() {
        // Can execute write tools
        assertTrue(registry.canExecute("updateModuleMode", auth(Role.ADMIN)));
        assertTrue(registry.canExecute("updateModuleOutputState", auth(Role.ADMIN)));

        // Can execute restricted-read tools (everything not public-read or write)
        assertTrue(registry.canExecute("getSystemSummary", auth(Role.ADMIN)));
        assertTrue(registry.canExecute("getHeatingSummary", auth(Role.ADMIN)));

        // Can execute public-read tools
        assertTrue(registry.canExecute("getTempStat", auth(Role.ADMIN)));
        assertTrue(registry.canExecute("getPressureStat", auth(Role.ADMIN)));
        assertTrue(registry.canExecute("getPowerSummary", auth(Role.ADMIN)));
        assertTrue(registry.canExecute("getPowerVoltageStat", auth(Role.ADMIN)));
        assertTrue(registry.canExecute("getPowerVoltageExtStat", auth(Role.ADMIN)));

        // Can execute module tools
        assertTrue(registry.canExecute("getModuleList", auth(Role.ADMIN)));
        assertTrue(registry.canExecute("getModuleData", auth(Role.ADMIN)));
    }

    @Test
    void nonAdminCannotExecuteWriteTools() {
        assertFalse(registry.canExecute("updateModuleMode", auth(Role.UNDEFINED)));
        assertFalse(registry.canExecute("updateModuleOutputState", auth(Role.UNDEFINED)));
        assertTrue(registry.canExecute("getSystemSummary", auth(Role.UNDEFINED)));
    }

    @Test
    void authorizedGuestCannotExecuteRestrictedOrWriteTools() {
        // Write tools — denied
        assertFalse(registry.canExecute("updateModuleMode", auth(Role.AUTHORIZED_GUEST)));
        assertFalse(registry.canExecute("updateModuleOutputState", auth(Role.AUTHORIZED_GUEST)));

        // Restricted-read tools — denied (includes getPowerConsumptionStat and getPowerVoltageStat
        // which include internal data not on the guest controller)
        assertFalse(registry.canExecute("getSystemSummary", auth(Role.AUTHORIZED_GUEST)));
        assertFalse(registry.canExecute("getHeatingSummary", auth(Role.AUTHORIZED_GUEST)));
        assertFalse(registry.canExecute("getLuminosityStat", auth(Role.AUTHORIZED_GUEST)));
        assertFalse(registry.canExecute("getSystemStat", auth(Role.AUTHORIZED_GUEST)));
        assertFalse(registry.canExecute("getModuleList", auth(Role.AUTHORIZED_GUEST)));
        assertFalse(registry.canExecute("getModuleData", auth(Role.AUTHORIZED_GUEST)));
        assertFalse(registry.canExecute("getModuleListByGroup", auth(Role.AUTHORIZED_GUEST)));
        assertFalse(registry.canExecute("getBoilerTempStat", auth(Role.AUTHORIZED_GUEST)));
        assertFalse(registry.canExecute("getPowerConsumptionStat", auth(Role.AUTHORIZED_GUEST),
                "getPowerConsumptionStat includes internal consumption data, denied for AUTHORIZED_GUEST"));
        assertFalse(registry.canExecute("getPowerVoltageStat", auth(Role.AUTHORIZED_GUEST),
                "getPowerVoltageStat includes internal voltage data, denied for AUTHORIZED_GUEST"));

        // Public-read tools (outdoor temp/humidity, pressure, external power supply) — allowed
        assertTrue(registry.canExecute("getTempStat", auth(Role.AUTHORIZED_GUEST)));
        assertTrue(registry.canExecute("getPressureStat", auth(Role.AUTHORIZED_GUEST)));
        assertTrue(registry.canExecute("getPowerSummary", auth(Role.AUTHORIZED_GUEST)));
        assertTrue(registry.canExecute("getPowerVoltageExtStat", auth(Role.AUTHORIZED_GUEST),
                "getPowerVoltageExtStat is external voltage only, matches guest controller"));
    }

    @Test
    void authorizedGuestWithUndefinedShouldStillBeRestricted() {
        // Mixed guest+undefined should still be treated as guest-only
        assertFalse(registry.canExecute("getModuleList", auth(Role.AUTHORIZED_GUEST, Role.UNDEFINED)));
        assertFalse(registry.canExecute("updateModuleMode", auth(Role.AUTHORIZED_GUEST, Role.UNDEFINED)));
        assertTrue(registry.canExecute("getPressureStat", auth(Role.AUTHORIZED_GUEST, Role.UNDEFINED)));
        assertFalse(registry.canExecute("getSystemSummary", auth(Role.AUTHORIZED_GUEST, Role.UNDEFINED)));
    }

    @Test
    void supervisorCanExecuteRestrictedAndWriteTools() {
        // Write tools — allowed for updateModuleOutputState (module-assignment enforcement happens at Layer 3)
        assertTrue(registry.canExecute("updateModuleOutputState", auth(Role.SUPERVISOR)),
                "Supervisor should be able to execute updateModuleOutputState");

        // ADMIN_ONLY tool — denied for supervisor
        assertFalse(registry.canExecute("updateModuleMode", auth(Role.SUPERVISOR)),
                "Supervisor should NOT be able to execute ADMIN_ONLY tool updateModuleMode");

        // Restricted-read tools — allowed
        assertTrue(registry.canExecute("getSystemSummary", auth(Role.SUPERVISOR)));
        assertTrue(registry.canExecute("getModuleList", auth(Role.SUPERVISOR)));
        assertTrue(registry.canExecute("getModuleData", auth(Role.SUPERVISOR)));
        assertTrue(registry.canExecute("getBoilerTempStat", auth(Role.SUPERVISOR)));

        // Public-read tools — allowed
        assertTrue(registry.canExecute("getTempStat", auth(Role.SUPERVISOR)));
        assertTrue(registry.canExecute("getPressureStat", auth(Role.SUPERVISOR)));
        assertTrue(registry.canExecute("getPowerSummary", auth(Role.SUPERVISOR)));
    }

    @Test
    void unknownToolShouldNotBeExecutable() {
        assertFalse(registry.canExecute("nonexistentTool", auth(Role.ADMIN)));
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
        assertEquals(McpToolAccessType.ADMIN_ONLY, registry.getTool("updateModuleMode").get().accessType());
        assertEquals(McpToolAccessType.WRITE, registry.getTool("updateModuleOutputState").get().accessType());
    }
}
