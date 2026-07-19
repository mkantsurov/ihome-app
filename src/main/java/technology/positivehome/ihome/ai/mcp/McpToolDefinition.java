package technology.positivehome.ihome.ai.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import technology.positivehome.ihome.security.model.user.Role;

import java.util.Set;
import java.util.function.Function;

/**
 * Defines an MCP tool with its name, description, JSON Schema for parameters,
 * the set of roles required to execute it, an access type (READ/WRITE), and an
 * optional result transformer.
 *
 * <p>The {@code accessType} categorises the tool by the kind of operation it
 * performs. See {@link McpToolAccessType} for the available tiers.
 *
 * <p>The {@code resultTransformer} is applied to the JSON response before it is
 * returned to the AI model. Use it to convert raw sensor values into
 * human-readable form (e.g., temperature in hundredths of degrees → decimal degrees).
 *
 * <p>The defining characteristic of {@link McpToolAccessType#PUBLIC_READ} is that
 * the tool's data is <b>also served by the guest controller</b> (unauthenticated).
 * Within the AI chat this makes it accessible to all authenticated users, but that
 * is a consequence of the guest-controller exposure, not the primary definition.
 *
 * <p>Permission model:
 * <ul>
 *   <li>{@link McpToolAccessType#PUBLIC_READ} — data <b>also</b> exposed via the
 *       guest controller. Within the AI chat: accessible to all authenticated users
 *       (including {@code AUTHORIZED_GUEST}).</li>
 *   <li>{@link McpToolAccessType#RESTRICTED_READ} — data <b>not</b> on the guest
 *       controller. Within the AI chat: hidden from {@code AUTHORIZED_GUEST},
 *       available to all other authenticated roles.</li>
 *   <li>{@link McpToolAccessType#WRITE} — requires a role that carries MODULE WRITE
 *       permission ({@code ADMIN} or {@code SUPERVISOR})</li>
 * </ul>
 */
public record McpToolDefinition(
        String name,
        String description,
        McpToolAccessType accessType,
        JsonNode inputSchema,
        Set<Role> requiredRoles,
        Function<JsonNode, JsonNode> resultTransformer
) {
    public McpToolDefinition(String name, String description, McpToolAccessType accessType,
                             JsonNode inputSchema, Set<Role> requiredRoles) {
        this(name, description, accessType, inputSchema, requiredRoles, null);
    }

    /**
     * Returns true if this tool is a read-only tool (not {@link McpToolAccessType#WRITE}).
     */
    public boolean isReadOnly() {
        return accessType != McpToolAccessType.WRITE;
    }

    /**
     * Returns true if this tool's data is <b>also</b> served by the unauthenticated
     * guest controller (matches {@link McpToolAccessType#PUBLIC_READ}).
     */
    public boolean isPublicRead() {
        return accessType == McpToolAccessType.PUBLIC_READ;
    }

    /**
     * Returns true if this tool's data is <b>not</b> on the guest controller,
     * which within the AI chat means it is restricted from {@code AUTHORIZED_GUEST}
     * (matches {@link McpToolAccessType#RESTRICTED_READ}).
     */
    public boolean isRestrictedRead() {
        return accessType == McpToolAccessType.RESTRICTED_READ;
    }

    /**
     * Returns true if this tool requires a role that carries MODULE WRITE permission
     * (currently ADMIN or SUPERVISOR).
     */
    public boolean requiresModuleWritePermission() {
        if (requiredRoles == null) return false;
        return requiredRoles.contains(Role.ADMIN) || requiredRoles.contains(Role.SUPERVISOR);
    }
}
