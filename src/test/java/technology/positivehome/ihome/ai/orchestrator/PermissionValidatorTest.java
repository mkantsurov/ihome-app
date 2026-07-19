package technology.positivehome.ihome.ai.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import technology.positivehome.ihome.ai.mcp.McpToolRegistry;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.security.service.PermissionService;
import technology.positivehome.ihome.server.persistence.ModuleConfigRepository;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PermissionValidator}.
 */
@ExtendWith(MockitoExtension.class)
class PermissionValidatorTest {

    @Mock
    private Authentication authentication;

    @Mock
    private ModuleConfigRepository moduleConfigRepository;

    private PermissionValidator validator;
    private McpToolRegistry registry;

    @BeforeEach
    void setUp() {
        PermissionService permissionService = new PermissionService(moduleConfigRepository);
        registry = new McpToolRegistry(new ObjectMapper(), permissionService);
        registry.registerTools();
        validator = new PermissionValidator(registry);
    }

    @Test
    void shouldDenyWhenNotAuthenticated() {
        assertFalse(validator.canExecute("getSystemSummary", null));
    }

    @Test
    void shouldDenyWhenAuthenticationIsNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        assertFalse(validator.canExecute("getSystemSummary", authentication));
    }

    @Test
    void adminShouldExecuteAdminTools() {
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Role.ADMIN.authority())
        );
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getName()).thenReturn("admin");

        assertTrue(validator.canExecute("updateModuleMode", authentication));
        assertTrue(validator.canExecute("updateModuleOutputState", authentication));
    }

    @Test
    void nonAdminShouldNotExecuteAdminTools() {
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Role.UNDEFINED.authority())
        );
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getName()).thenReturn("user");

        assertFalse(validator.canExecute("updateModuleMode", authentication));
        assertFalse(validator.canExecute("updateModuleOutputState", authentication));
    }

    @Test
    void nonAdminShouldExecuteReadOnlyTools() {
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Role.UNDEFINED.authority())
        );
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertTrue(validator.canExecute("getSystemSummary", authentication));
        assertTrue(validator.canExecute("getPowerSummary", authentication));
        assertTrue(validator.canExecute("getTempStat", authentication));
    }

    @Test
    void shouldReturnAllowedToolsForAdmin() {
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Role.ADMIN.authority())
        );
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        List<String> allowedTools = validator.getAllowedTools(authentication);
        assertTrue(allowedTools.contains("updateModuleMode"));
        assertTrue(allowedTools.contains("getSystemSummary"));
    }

    @Test
    void shouldReturnOnlyReadOnlyToolsForNonAdmin() {
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Role.UNDEFINED.authority())
        );
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        List<String> allowedTools = validator.getAllowedTools(authentication);
        assertFalse(allowedTools.contains("updateModuleMode"));
        assertTrue(allowedTools.contains("getSystemSummary"));
    }

    @Test
    void shouldReturnEmptyListForNullAuthentication() {
        List<String> allowedTools = validator.getAllowedTools(null);
        assertTrue(allowedTools.isEmpty());
    }
}
