package technology.positivehome.ihome.ai.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.security.model.user.Role;

import java.util.*;
import java.util.function.Function;

/**
 * Registry of all MCP tools available for home automation.
 * Each tool has a name, description, JSON Schema for parameters, and required roles.
 *
 * Tools are categorized as:
 * - Read-only: any authenticated user can execute
 * - Admin-only: requires ROLE_ADMIN
 */
@Component
public class McpToolRegistry {

    private static final Logger log = LoggerFactory.getLogger(McpToolRegistry.class);

    private final Map<String, McpToolDefinition> tools = new LinkedHashMap<>();
    private final ObjectMapper objectMapper;

    public McpToolRegistry(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void registerTools() {
        registerReadOnlyTools();
        registerAdminTools();
        log.info("Registered {} MCP tools ({} read-only, {} admin-only)",
                tools.size(),
                tools.values().stream().filter(McpToolDefinition::isReadOnly).count(),
                tools.values().stream().filter(McpToolDefinition::requiresAdmin).count());
    }

    private void registerReadOnlyTools() {
        // System summary
        register("getSystemSummary",
                "Get the overall system summary including power, heating, and security status",
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // Power summary
        register("getPowerSummary",
                "Get detailed power consumption and supply information",
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // Heating summary
        register("getHeatingSummary",
                "Get heating system status and temperature information",
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // Module list
        register("getModuleList",
                "Get a list of all home automation modules, optionally filtered by assignment or group",
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
                buildObjectSchema()
                        .put("type", "object")
                        .<ObjectNode>set("properties", objectMapper.createObjectNode()
                                .<ObjectNode>set("moduleId", objectMapper.createObjectNode()
                                        .put("type", "integer")
                                        .put("description", "The ID of the module to retrieve")))
                        .<ObjectNode>set("required", objectMapper.createArrayNode().add("moduleId")),
                Set.of());

        // Temperature statistics (values in hundredths of degrees, scaled to decimal)
        register("getTempStat",
                "Get temperature statistics across all sensors. Values are in degrees Celsius.",
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of(),
                scaleChartPointValues(0.01));

        // Power consumption statistics
        register("getPowerConsumptionStat",
                "Get power consumption statistics over time. Values are in watts.",
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // Pressure statistics (values in hundredths of mmHg, scaled to decimal)
        register("getPressureStat",
                "Get atmospheric pressure statistics. Values are in mmHg.",
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of(),
                scaleChartPointValues(0.01));

        // Luminosity statistics
        register("getLuminosityStat",
                "Get luminosity/light level statistics. Values are in lux.",
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // System statistics (heap memory in bytes — no scaling needed)
        register("getSystemStat",
                "Get overall system statistics. Values are in bytes.",
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of());

        // Boiler temperature statistics (values in hundredths of degrees, scaled to decimal)
        register("getBoilerTempStat",
                "Get boiler temperature statistics. Values are in degrees Celsius.",
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of(),
                scaleChartPointValues(0.01));

        // Power voltage statistics (values in hundredths of volts, scaled to decimal)
        register("getPowerVoltageStat",
                "Get power voltage statistics. Values are in volts.",
                buildObjectSchema()
                        .put("type", "object")
                        .set("properties", objectMapper.createObjectNode()),
                Set.of(),
                scaleChartPointValues(0.01));

        // Module list by group
        register("getModuleListByGroup",
                "Get a list of modules belonging to a specific group",
                buildObjectSchema()
                        .put("type", "object")
                        .<ObjectNode>set("properties", objectMapper.createObjectNode()
                                .<ObjectNode>set("group", objectMapper.createObjectNode()
                                        .put("type", "integer")
                                        .put("description", "The group ID to filter by")))
                        .<ObjectNode>set("required", objectMapper.createArrayNode().add("group")),
                Set.of());
    }

    private void registerAdminTools() {
        Set<Role> adminOnly = Set.of(Role.ADMIN);

        // Update module mode
        register("updateModuleMode",
                "Change the operation mode of a module (e.g., AUTO, MANUAL, OFF)",
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
                adminOnly);

        // Update module output state
        register("updateModuleOutputState",
                "Turn a module's output ON or OFF",
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
                adminOnly);
    }

    private void register(String name, String description, ObjectNode inputSchema, Set<Role> requiredRoles) {
        register(name, description, inputSchema, requiredRoles, null);
    }

    private void register(String name, String description, ObjectNode inputSchema, Set<Role> requiredRoles,
                          Function<JsonNode, JsonNode> resultTransformer) {
        tools.put(name, new McpToolDefinition(name, description, inputSchema, requiredRoles, resultTransformer));
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
     * Returns tool definitions filtered by the user's roles.
     * Users with ADMIN role see all tools; others see only read-only tools.
     */
    public List<McpToolDefinition> getToolsForRoles(Collection<? extends org.springframework.security.core.GrantedAuthority> authorities) {
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return tools.values().stream()
                .filter(tool -> isAdmin || !tool.requiresAdmin())
                .toList();
    }

    /**
     * Returns a specific tool definition by name.
     */
    public Optional<McpToolDefinition> getTool(String name) {
        return Optional.ofNullable(tools.get(name));
    }

    /**
     * Checks if a user (by their authorities) is allowed to execute a given tool.
     */
    public boolean canExecute(String toolName, Collection<? extends org.springframework.security.core.GrantedAuthority> authorities) {
        McpToolDefinition tool = tools.get(toolName);
        if (tool == null) {
            return false;
        }
        if (!tool.requiresAdmin()) {
            return true; // Read-only tool — any authenticated user
        }
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
