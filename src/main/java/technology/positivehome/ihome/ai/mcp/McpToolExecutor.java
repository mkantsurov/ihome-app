package technology.positivehome.ihome.ai.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.server.processor.StatisticProcessor;
import technology.positivehome.ihome.server.processor.SystemProcessor;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Executes MCP tool calls by delegating to the existing system processors.
 * This is the bridge between the MCP protocol layer and the actual home automation logic.
 */
@Component
public class McpToolExecutor {

    private static final Logger log = LoggerFactory.getLogger(McpToolExecutor.class);

    private final SystemProcessor systemProcessor;
    private final StatisticProcessor statisticProcessor;
    private final McpToolRegistry toolRegistry;
    private final ObjectMapper objectMapper;

    public McpToolExecutor(SystemProcessor systemProcessor,
                           StatisticProcessor statisticProcessor,
                           McpToolRegistry toolRegistry,
                           ObjectMapper objectMapper) {
        this.systemProcessor = systemProcessor;
        this.statisticProcessor = statisticProcessor;
        this.toolRegistry = toolRegistry;
        this.objectMapper = objectMapper;
    }

    /**
     * Executes a tool by name with the given arguments.
     *
     * @param toolName  the name of the tool to execute
     * @param arguments the JSON arguments for the tool
     * @return the result as a JSON node
     */
    public JsonNode execute(String toolName, JsonNode arguments) {
        log.debug("Executing MCP tool: {} with args: {}", toolName, arguments);
        try {
            Object result = switch (toolName) {
                case "getSystemSummary" -> systemProcessor.getSummaryInfo();
                case "getPowerSummary" -> systemProcessor.getPowerSummaryInfo();
                case "getHeatingSummary" -> systemProcessor.getHeatingSummaryInfo();
                case "getModuleList" -> {
                    Integer assignment = getIntArg(arguments, "assignment");
                    Long group = getLongArg(arguments, "group");
                    yield systemProcessor.getModuleList(assignment, group);
                }
                case "getModuleData" -> {
                    long moduleId = getRequiredLongArg(arguments, "moduleId");
                    yield systemProcessor.getModuleData(moduleId);
                }
                case "getTempStat" -> statisticProcessor.getTempStat();
                case "getPowerConsumptionStat" -> statisticProcessor.getPowerConsumptionStat();
                case "getPressureStat" -> statisticProcessor.getPressureStat();
                case "getLuminosityStat" -> statisticProcessor.getLuminosityStat();
                case "getSystemStat" -> statisticProcessor.getSystemStat();
                case "getBoilerTempStat" -> statisticProcessor.getBoilerTempStat();
                case "getPowerVoltageStat" -> statisticProcessor.getPowerVoltageStat();
                case "getModuleListByGroup" -> {
                    long group = getRequiredLongArg(arguments, "group");
                    yield systemProcessor.getModuleListByGroup(group);
                }
                case "updateModuleMode" -> {
                    long moduleId = getRequiredLongArg(arguments, "moduleId");
                    int mode = getRequiredIntArg(arguments, "mode");
                    yield systemProcessor.updateModuleMode(moduleId, mode);
                }
                case "updateModuleOutputState" -> {
                    long moduleId = getRequiredLongArg(arguments, "moduleId");
                    int state = getRequiredIntArg(arguments, "state");
                    yield systemProcessor.updateModuleOutputState(moduleId, state);
                }
                default -> throw new IllegalArgumentException("Unknown tool: " + toolName);
            };

            JsonNode resultNode = objectMapper.valueToTree(result);
            ObjectNode response = objectMapper.createObjectNode();
            response.set("data", resultNode);
            response.put("success", true);

            // Apply result transformer if defined for this tool
            McpToolDefinition definition = toolRegistry.getTool(toolName).orElse(null);
            if (definition != null && definition.resultTransformer() != null) {
                return definition.resultTransformer().apply(response);
            }
            return response;

        } catch (MegadApiMallformedUrlException | MegadApiMallformedResponseException |
                 PortNotSupporttedFunctionException | IOException | InterruptedException |
                 URISyntaxException e) {
            log.error("Error executing tool {}: {}", toolName, e.getMessage(), e);
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to execute " + toolName + ": " + e.getMessage());
            return errorResponse;
        }
    }

    private Long getLongArg(JsonNode arguments, String name) {
        if (arguments == null || !arguments.has(name) || arguments.get(name).isNull()) {
            return null;
        }
        return arguments.get(name).asLong();
    }

    private long getRequiredLongArg(JsonNode arguments, String name) {
        if (arguments == null || !arguments.has(name)) {
            throw new IllegalArgumentException("Missing required argument: " + name);
        }
        return arguments.get(name).asLong();
    }

    private Integer getIntArg(JsonNode arguments, String name) {
        if (arguments == null || !arguments.has(name) || arguments.get(name).isNull()) {
            return null;
        }
        return arguments.get(name).asInt();
    }

    private int getRequiredIntArg(JsonNode arguments, String name) {
        if (arguments == null || !arguments.has(name)) {
            throw new IllegalArgumentException("Missing required argument: " + name);
        }
        return arguments.get(name).asInt();
    }
}
