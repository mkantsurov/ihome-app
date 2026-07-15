package technology.positivehome.ihome.ai.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import technology.positivehome.ihome.security.model.user.Role;

import java.util.Set;

/**
 * Defines an MCP tool with its name, description, JSON Schema for parameters,
 * and the set of roles required to execute it.
 */
public record McpToolDefinition(
        String name,
        String description,
        JsonNode inputSchema,
        Set<Role> requiredRoles
) {
    /**
     * Returns true if this tool requires only authentication (any authenticated user can use it).
     */
    public boolean isReadOnly() {
        return requiredRoles == null || requiredRoles.isEmpty();
    }

    /**
     * Returns true if this tool requires the ADMIN role.
     */
    public boolean requiresAdmin() {
        return requiredRoles != null && requiredRoles.contains(Role.ADMIN);
    }
}
