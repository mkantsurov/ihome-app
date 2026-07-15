package technology.positivehome.ihome.ai.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.ai.mcp.model.JsonRpcRequest;
import technology.positivehome.ihome.ai.mcp.model.JsonRpcResponse;

/**
 * Handles JSON-RPC 2.0 requests for the MCP server.
 * Implements the MCP methods: initialize, tools/list, tools/call.
 */
@Component
public class McpJsonRpcHandler {

    private static final Logger log = LoggerFactory.getLogger(McpJsonRpcHandler.class);

    private static final String METHOD_INITIALIZE = "initialize";
    private static final String METHOD_TOOLS_LIST = "tools/list";
    private static final String METHOD_TOOLS_CALL = "tools/call";

    private static final int ERROR_METHOD_NOT_FOUND = -32601;
    private static final int ERROR_INVALID_PARAMS = -32602;
    private static final int ERROR_INTERNAL = -32603;
    private static final int ERROR_PARSE = -32700;

    private final McpToolRegistry toolRegistry;
    private final McpToolExecutor toolExecutor;
    private final ObjectMapper objectMapper;

    public McpJsonRpcHandler(McpToolRegistry toolRegistry,
                             McpToolExecutor toolExecutor,
                             ObjectMapper objectMapper) {
        this.toolRegistry = toolRegistry;
        this.toolExecutor = toolExecutor;
        this.objectMapper = objectMapper;
    }

    /**
     * Handles a raw JSON-RPC request string and returns the response string.
     */
    public String handle(String requestBody) {
        try {
            JsonRpcRequest request = objectMapper.readValue(requestBody, JsonRpcRequest.class);
            JsonRpcResponse response = dispatch(request);
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON-RPC request: {}", e.getMessage());
            return buildErrorResponse(null, ERROR_PARSE, "Parse error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error handling JSON-RPC request: {}", e.getMessage(), e);
            return buildErrorResponse(null, ERROR_INTERNAL, "Internal error: " + e.getMessage());
        }
    }

    private JsonRpcResponse dispatch(JsonRpcRequest request) {
        String method = request.method();
        String id = request.id();

        log.debug("Dispatching JSON-RPC method: {}", method);

        return switch (method) {
            case METHOD_INITIALIZE -> handleInitialize(id, request.params());
            case METHOD_TOOLS_LIST -> handleToolsList(id);
            case METHOD_TOOLS_CALL -> handleToolsCall(id, request.params());
            default -> JsonRpcResponse.error(id, ERROR_METHOD_NOT_FOUND,
                    "Method not found: " + method);
        };
    }

    /**
     * Handles the MCP initialize method.
     * Returns server capabilities.
     */
    private JsonRpcResponse handleInitialize(String id, JsonNode params) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("protocolVersion", "2024-11-05");

        ObjectNode capabilities = objectMapper.createObjectNode();
        ObjectNode toolsCap = objectMapper.createObjectNode();
        toolsCap.put("listChanged", false);
        capabilities.set("tools", toolsCap);
        result.set("capabilities", capabilities);

        ObjectNode serverInfo = objectMapper.createObjectNode();
        serverInfo.put("name", "ihome-mcp-server");
        serverInfo.put("version", "1.0.0");
        result.set("serverInfo", serverInfo);

        return JsonRpcResponse.success(id, result);
    }

    /**
     * Handles the MCP tools/list method.
     * Returns all registered tool definitions.
     */
    private JsonRpcResponse handleToolsList(String id) {
        ArrayNode toolsArray = objectMapper.createArrayNode();

        for (McpToolDefinition tool : toolRegistry.getAllTools()) {
            ObjectNode toolNode = objectMapper.createObjectNode();
            toolNode.put("name", tool.name());
            toolNode.put("description", tool.description());
            toolNode.set("inputSchema", tool.inputSchema());
            toolsArray.add(toolNode);
        }

        ObjectNode result = objectMapper.createObjectNode();
        result.set("tools", toolsArray);

        return JsonRpcResponse.success(id, result);
    }

    /**
     * Handles the MCP tools/call method.
     * Executes a tool by name with the given arguments.
     */
    private JsonRpcResponse handleToolsCall(String id, JsonNode params) {
        if (params == null || !params.has("name")) {
            return JsonRpcResponse.error(id, ERROR_INVALID_PARAMS,
                    "Missing required parameter: name");
        }

        String toolName = params.get("name").asText();
        JsonNode arguments = params.has("arguments") ? params.get("arguments") : null;

        // Verify tool exists
        if (toolRegistry.getTool(toolName).isEmpty()) {
            return JsonRpcResponse.error(id, ERROR_INVALID_PARAMS,
                    "Unknown tool: " + toolName);
        }

        try {
            JsonNode toolResult = toolExecutor.execute(toolName, arguments);

            ObjectNode result = objectMapper.createObjectNode();
            ArrayNode content = objectMapper.createArrayNode();
            ObjectNode contentItem = objectMapper.createObjectNode();
            contentItem.put("type", "text");
            contentItem.put("text", toolResult.toString());
            content.add(contentItem);
            result.set("content", content);

            return JsonRpcResponse.success(id, result);
        } catch (Exception e) {
            log.error("Tool execution failed: {}", e.getMessage(), e);
            return JsonRpcResponse.error(id, ERROR_INTERNAL,
                    "Tool execution failed: " + e.getMessage());
        }
    }

    private String buildErrorResponse(String id, int code, String message) {
        try {
            JsonRpcResponse response = JsonRpcResponse.error(id, code, message);
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return "{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32603,\"message\":\"Internal error\"},\"id\":null}";
        }
    }
}
