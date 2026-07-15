package technology.positivehome.ihome.ai.orchestrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.ai.mcp.McpToolRegistry;

import java.util.Collection;

/**
 * Validates that the authenticated user has permission to execute a given MCP tool.
 * This is the security boundary between the LLM's tool_call decisions and actual execution.
 */
@Component
public class PermissionValidator {

    private static final Logger log = LoggerFactory.getLogger(PermissionValidator.class);

    private final McpToolRegistry toolRegistry;

    public PermissionValidator(McpToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    /**
     * Checks whether the authenticated user can execute the specified tool.
     *
     * @param toolName       the name of the tool to check
     * @param authentication the user's authentication (from SecurityContext)
     * @return true if the user is allowed to execute the tool
     */
    public boolean canExecute(String toolName, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Permission denied for tool '{}': user not authenticated", toolName);
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean allowed = toolRegistry.canExecute(toolName, authorities);

        if (!allowed) {
            log.warn("Permission denied for tool '{}': user '{}' lacks required role (has: {})",
                    toolName, authentication.getName(), authorities);
        }

        return allowed;
    }

    /**
     * Returns the list of tool names the user is allowed to execute.
     */
    public java.util.List<String> getAllowedTools(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return java.util.List.of();
        }

        return toolRegistry.getToolsForRoles(authentication.getAuthorities()).stream()
                .map(tool -> tool.name())
                .toList();
    }
}
