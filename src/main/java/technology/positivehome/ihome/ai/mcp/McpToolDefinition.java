package technology.positivehome.ihome.ai.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import technology.positivehome.ihome.security.model.user.Role;

import java.util.Set;
import java.util.function.Function;

/**
 * Defines an MCP tool with its name, description, JSON Schema for parameters,
 * the set of roles required to execute it, and an optional result transformer.
 *
 * <p>The {@code resultTransformer} is applied to the JSON response before it is
 * returned to the AI model. Use it to convert raw sensor values into
 * human-readable form (e.g., temperature in hundredths of degrees → decimal degrees).
 */
public record McpToolDefinition(
        String name,
        String description,
        JsonNode inputSchema,
        Set<Role> requiredRoles,
        Function<JsonNode, JsonNode> resultTransformer
) {
    public McpToolDefinition(String name, String description, JsonNode inputSchema, Set<Role> requiredRoles) {
        this(name, description, inputSchema, requiredRoles, null);
    }

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
