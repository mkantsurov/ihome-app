package technology.positivehome.ihome.ai.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.security.service.PermissionService;

import java.util.*;
import java.util.function.Function;

/**
 * Registry of all MCP tools available for home automation.
 * Each tool has a name, description, JSON Schema for parameters, access type, and required roles.
 *
 * <p>{@link McpToolAccessType#PUBLIC_READ} marks tools whose data is <b>also</b> served
 * by the unauthenticated guest controller — that is its defining characteristic.
 * Within the AI chat this makes the tool accessible to all authenticated users, but
 * that is a consequence, not the primary definition.
 *
 * <p>Three access tiers exist within the AI chat:
 * <ul>
 *   <li><b>PUBLIC_READ</b> — data <b>also</b> on the guest controller.
 *       Within the chat: accessible to all authenticated users (including {@code AUTHORIZED_GUEST}).</li>
 *   <li><b>RESTRICTED_READ</b> — data <b>not</b> on the guest controller.
 *       Within the chat: hidden from {@code AUTHORIZED_GUEST}, available to all other roles.</li>
 *   <li><b>WRITE</b> — requires {@code ROLE_ADMIN} or {@code ROLE_SUPERVISOR}</li>
 * </ul>
 */
@Component
public class McpToolRegistry {

    private static final Logger log = LoggerFactory.getLogger(McpToolRegistry.class);

    private final Map<String, McpToolDefinition> tools = new LinkedHashMap<>();
    private final ObjectMapper objectMapper;
    private final PermissionService permissionService;

    public McpToolRegistry(ObjectMapper objectMapper, PermissionService permissionService) {
        this.objectMapper = objectMapper;
        this.permissionService = permissionService;
    }

    @PostConstruct
    public void registerTools() {
        registerPublicReadTools();
        registerRestrictedReadTools();
        registerWriteTools();
        log.info("Registered {} MCP tools ({} public-read, {} restricted-read, {} write)",
                tools.size(),
                tools.values().stream().filter(t -> t.accessType() == McpToolAccessType.PUBLIC_READ).count(),
                tools.values().stream().filter(t -> t.accessType() == McpToolAccessType.RESTRICTED_READ).count(),
                tools.values().stream().filter(t -> !t.isReadOnly()).count());
    }

    /**
     * Registers tools whose data is also exposed via the unauthenticated guest controller.
     * Only tools that directly map to guest-controller endpoints are registered here:
     * <ul>
     *   <li>Atmospheric pressure ({@code /guest-api/v1/stats/pressure-stat})</li>
     * </ul>
     * Within the AI chat these are available to all authenticated users (including AUTHORIZED_GUEST).
     */
    private void registerPublicReadTools() {
        // Temperature statistics — includes outdoor temperature/humidity data (also on guest controller)
        // Guest controller: GET /guest-api/v1/stats/outdoor-temp-stat
        // Values in hundredths of degrees, scaled to decimal
        register("getTempStat",
                "Get temperature statistics across all sensors. Values are in degrees Celsius.",
                McpToolAccessType.PUBLIC_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of(),
                scaleChartPointValues(0.01));

        // Pressure statistics — exact match to guest controller
        // Guest controller: GET /guest-api/v1/stats/pressure-stat
        // Values in hundredths of mmHg, scaled to decimal
        register("getPressureStat",
                "Get atmospheric pressure statistics. Values are in mmHg.",
                McpToolAccessType.PUBLIC_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of(),
                scaleChartPointValues(0.01));

        // Power summary — includes external power supply data (also on guest controller)
        // Guest controller: GET /guest-api/v1/stats/power-summary
        register("getPowerSummary",
                "Get detailed power consumption and supply information",
                McpToolAccessType.PUBLIC_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // Power consumption statistics — includes external consumption data (also on guest controller)
        // Guest controller: GET /guest-api/v1/stats/power-stat and GET /guest-api/v1/stats/power-summary
        register("getPowerConsumptionStat",
                "Get power consumption statistics over time. Values are in watts.",
                McpToolAccessType.PUBLIC_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // Power voltage statistics — includes external voltage data (also on guest controller)
        // Guest controller: GET /guest-api/v1/stats/power-stat
        // Values in hundredths of volts, scaled to decimal
        register("getPowerVoltageStat",
                "Get power voltage statistics. Values are in volts.",
                McpToolAccessType.PUBLIC_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of(),
                scaleChartPointValues(0.01));
    }

    /**
     * Registers read-only tools that are NOT exposed via the guest controller.
     * These cover all detailed system information and are available to all authenticated
     * users <b>except</b> {@code AUTHORIZED_GUEST} (e.g., people living at home).
     * <p>
     * The guest controller only exposes outdoor temperature/humidity, pressure, external
     * power voltage, and external power summary. Everything else is restricted-read.
     */
    private void registerRestrictedReadTools() {
        // System summary
        register("getSystemSummary",
                "Get the overall system summary including power, heating, and security status",
                McpToolAccessType.RESTRICTED_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // Heating summary
        register("getHeatingSummary",
                "Get heating system status and temperature information",
                McpToolAccessType.RESTRICTED_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // Luminosity statistics
        register("getLuminosityStat",
                "Get luminosity/light level statistics. Values are in lux.",
                McpToolAccessType.RESTRICTED_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // System statistics (heap memory in bytes — no scaling needed)
        register("getSystemStat",
                "Get overall system statistics. Values are in bytes.",
                McpToolAccessType.RESTRICTED_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // Module list
        register("getModuleList",
                "Get a list of all home automation modules, optionally filtered by assignment or group",
                McpToolAccessType.RESTRICTED_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .<ObjectNode>set("properties", objectMapper.createObjectNode()
                                .<ObjectNode>set("assignment", objectMapper.createObjectNode()
                                        .put("type", "integer")
                                        .put("description", "Optional filter by module assignment type"))
                                .<ObjectNode>set("group", objectMapper.createObjectNode()
                                        .put("type", "integer")
                                        .put("description", "Optional filter by module group ID")))
                        .set("required", objectMapper.createArrayNode()),
                Set.of());

        // Module data
        register("getModuleData",
                "Get detailed data for a specific module by its ID",
                McpToolAccessType.RESTRICTED_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .<ObjectNode>set("properties", objectMapper.createObjectNode()
                                .<ObjectNode>set("moduleId", objectMapper.createObjectNode()
                                        .put("type", "integer")
                                        .put("description", "The ID of the module to retrieve")))
                        .<ObjectNode>set("required", objectMapper.createArrayNode().add("moduleId")),
                Set.of());

        // Module list by group
        register("getModuleListByGroup",
                "Get a list of modules belonging to a specific group",
                McpToolAccessType.RESTRICTED_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .<ObjectNode>set("properties", objectMapper.createObjectNode()
                                .<ObjectNode>set("group", objectMapper.createObjectNode()
                                        .put("type", "integer")
                                        .put("description", "The group ID to filter by")))
                        .<ObjectNode>set("required", objectMapper.createArrayNode().add("group")),
                Set.of());

        // Boiler temperature statistics (values in hundredths of degrees, scaled to decimal)
        register("getBoilerTempStat",
                "Get boiler temperature statistics. Values are in degrees Celsius.",
                McpToolAccessType.RESTRICTED_READ,
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of(),
                scaleChartPointValues(0.01));
    }

    private void registerWriteTools() {
        Set<Role> moduleWriteRoles = Set.of(Role.ADMIN, Role.SUPERVISOR);

        // Update module mode
        register("updateModuleMode",
                "Change the operation mode of a module (e.g., AUTO, MANUAL, OFF)",
                McpToolAccessType.WRITE,
                buildObjectSchema()
                        .put("type", "object")
                        .<ObjectNode>set("properties", objectMapper.createObjectNode()
                                .<ObjectNode>set("moduleId", objectMapper.createObjectNode()
                                        .put("type", "integer")
                                        .put("description", "The ID of the module to update"))
                                .<ObjectNode>set("mode", objectMapper.createObjectNode()
                                        .put("type", "integer")
                                        .put("description", "The new operation mode (0=AUTO, 1=MANUAL, 2=OFF)")))
                        .<ObjectNode>set("required", objectMapper.createArrayNode()
                                .add("moduleId").add("mode")),
                moduleWriteRoles);

        // Update module output state
        register("updateModuleOutputState",
                "Enable or disable a module's power output (controls lights, garage door power, sliding gate power, etc.)",
                McpToolAccessType.WRITE,
                buildObjectSchema()
                        .put("type", "object")
                        .<ObjectNode>set("properties", objectMapper.createObjectNode()
                                .<ObjectNode>set("moduleId", objectMapper.createObjectNode()
                                        .put("type", "integer")
                                        .put("description", "The ID of the module to control"))
                                .<ObjectNode>set("state", objectMapper.createObjectNode()
                                        .put("type", "integer")
                                        .put("description", "The desired output state (0=OFF, 1=ON)")))
                        .<ObjectNode>set("required", objectMapper.createArrayNode()
                                .add("moduleId").add("state")),
                moduleWriteRoles);
    }

    private void register(String name, String description, McpToolAccessType accessType,
                          ObjectNode inputSchema, Set<Role> requiredRoles) {
        register(name, description, accessType, inputSchema, requiredRoles, null);
    }

    private void register(String name, String description, McpToolAccessType accessType,
                          ObjectNode inputSchema, Set<Role> requiredRoles,
                          Function<JsonNode, JsonNode> resultTransformer) {
        tools.put(name, new McpToolDefinition(name, description, accessType, inputSchema, requiredRoles, resultTransformer));
    }

    /**
     * Creates a transformer that scales all {@code value} fields in ChartPointInfo
     * nodes by the given factor. Use this when raw integer sensor values need to be
     * converted to human-readable form (e.g., temperature in hundredths of degrees
     * → decimal degrees with factor 0.01).
     */
    private static Function<JsonNode, JsonNode> scaleChartPointValues(double factor) {
        return root -> {
            if (root == null || !root.isObject()) return root;
            scaleValuesRecursive(root, factor);
            return root;
        };
    }

    private static void scaleValuesRecursive(JsonNode node, double factor) {
        if (node.isObject()) {
            var obj = (ObjectNode) node;
            if (obj.has("value") && obj.has("dt") && obj.get("value").isInt()) {
                int raw = obj.get("value").asInt();
                obj.put("value", raw * factor);
                obj.put("valueRaw", raw);
            }
            var iter = obj.fields();
            while (iter.hasNext()) {
                var entry = iter.next();
                scaleValuesRecursive(entry.getValue(), factor);
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                scaleValuesRecursive(child, factor);
            }
        }
    }

    private ObjectNode buildObjectSchema() {
        return objectMapper.createObjectNode();
    }

    /**
     * Returns all registered tool definitions.
     */
    public Collection<McpToolDefinition> getAllTools() {
        return Collections.unmodifiableCollection(tools.values());
    }

    /**
     * Returns tool definitions available to the given user.
     * <ul>
     *   <li><b>PUBLIC_READ</b> — all authenticated users (including AUTHORIZED_GUEST)</li>
     *   <li><b>RESTRICTED_READ</b> — all authenticated users except AUTHORIZED_GUEST</li>
     *   <li><b>WRITE</b> — requires {@code ROLE_ADMIN} or {@code ROLE_SUPERVISOR}</li>
     * </ul>
     * <p>
     * Note: Per-module READ/WRITE permissions are checked separately by
     * {@code PermissionService#hasModulePermission(Authentication, long, ...)}.
     */
    public List<McpToolDefinition> getToolsForRoles(Collection<? extends org.springframework.security.core.GrantedAuthority> authorities) {
        boolean canWriteModules = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ADMIN.authority())
                        || a.getAuthority().equals(Role.SUPERVISOR.authority()));

        boolean isGuestOnly = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(Role.AUTHORIZED_GUEST.authority()))
                && authorities.stream().noneMatch(a ->
                        a.getAuthority().equals(Role.ADMIN.authority())
                        || a.getAuthority().equals(Role.SUPERVISOR.authority())
                        || a.getAuthority().equals(Role.CHILDREN_ROOM1_MANAGER.authority())
                        || a.getAuthority().equals(Role.CHILDREN_ROOM2_MANAGER.authority()));

        return tools.values().stream()
                .filter(tool -> {
                    if (tool.accessType() == McpToolAccessType.WRITE) {
                        return canWriteModules;
                    }
                    if (tool.isRestrictedRead()) {
                        return !isGuestOnly;
                    }
                    return true; // PUBLIC_READ / READ
                })
                .toList();
    }

    /**
     * Returns the required {@link McpToolAccessType} for the given tool.
     *
     * @return the access type, or {@code null} if the tool is not registered
     */
    public McpToolAccessType getRequiredAccessType(String toolName) {
        McpToolDefinition tool = tools.get(toolName);
        return tool != null ? tool.accessType() : null;
    }

    /**
     * Returns a specific tool definition by name.
     */
    public Optional<McpToolDefinition> getTool(String name) {
        return Optional.ofNullable(tools.get(name));
    }

    /**
     * Checks if a user is allowed to execute a given tool.
     * <ul>
     *   <li>PUBLIC_READ tools: any authenticated user can execute them.</li>
     *   <li>RESTRICTED_READ tools: any authenticated user <b>except</b> AUTHORIZED_GUEST.</li>
     *   <li>WRITE tools: requires a role with any write permission (ADMIN or SUPERVISOR).
     *       SUPERVISOR write access is further restricted per module-assignment type at
     *       execution time — see Layer 3 enforcement in {@code ChatOrchestratorService}.</li>
     * </ul>
     */
    public boolean canExecute(String toolName, Authentication authentication) {
        McpToolDefinition tool = tools.get(toolName);
        if (tool == null) {
            return false;
        }
        if (tool.accessType() == McpToolAccessType.WRITE) {
            return permissionService.hasAnyWritePermission(authentication);
        }
        var authorities = authentication.getAuthorities();
        if (tool.isRestrictedRead()) {
            boolean isGuestOnly = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals(Role.AUTHORIZED_GUEST.authority()))
                    && authorities.stream().noneMatch(a ->
                            a.getAuthority().equals(Role.ADMIN.authority())
                            || a.getAuthority().equals(Role.SUPERVISOR.authority())
                            || a.getAuthority().equals(Role.CHILDREN_ROOM1_MANAGER.authority())
                            || a.getAuthority().equals(Role.CHILDREN_ROOM2_MANAGER.authority()));
            return !isGuestOnly;
        }
        // PUBLIC_READ / READ — any authenticated user
        return true;
    }
}
