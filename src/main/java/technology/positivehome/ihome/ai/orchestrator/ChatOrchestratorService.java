package technology.positivehome.ihome.ai.orchestrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import technology.positivehome.ihome.ai.deepseek.DeepSeekClient;
import technology.positivehome.ihome.ai.deepseek.model.*;
import technology.positivehome.ihome.ai.mcp.McpToolAccessType;
import technology.positivehome.ihome.ai.mcp.McpToolDefinition;
import technology.positivehome.ihome.ai.mcp.McpToolExecutor;
import technology.positivehome.ihome.ai.mcp.McpToolRegistry;
import technology.positivehome.ihome.model.runtime.module.ModuleSummary;
import technology.positivehome.ihome.security.service.PermissionService;
import technology.positivehome.ihome.security.util.IHomeApiTargetAccessType;
import technology.positivehome.ihome.server.processor.SystemProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Core orchestrator that manages the conversation between the user, DeepSeek LLM,
 * and the home automation tool execution layer.
 *
 * Flow:
 * 1. User sends a message → build prompt with available tools
 * 2. Send to DeepSeek → get response (text or tool_call)
 * 3. If tool_call: validate permissions → execute via McpToolExecutor → send result back to DeepSeek
 * 4. Repeat until DeepSeek returns a final text response
 * 5. Return the final response to the user
 *
 * Tool execution happens in-process via direct Spring bean calls — no HTTP overhead.
 */
@Service
public class ChatOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(ChatOrchestratorService.class);

    private static final int MAX_TOOL_CALL_ROUNDS = 5;

    private final DeepSeekClient deepSeekClient;
    private final McpToolRegistry toolRegistry;
    private final McpToolExecutor toolExecutor;
    private final PermissionValidator permissionValidator;
    private final PermissionService permissionService;
    private final SystemProcessor systemProcessor;
    private final ObjectMapper objectMapper;

    public ChatOrchestratorService(DeepSeekClient deepSeekClient,
                                   McpToolRegistry toolRegistry,
                                   McpToolExecutor toolExecutor,
                                   PermissionValidator permissionValidator,
                                   PermissionService permissionService,
                                   SystemProcessor systemProcessor,
                                   ObjectMapper objectMapper) {
        this.deepSeekClient = deepSeekClient;
        this.toolRegistry = toolRegistry;
        this.toolExecutor = toolExecutor;
        this.permissionValidator = permissionValidator;
        this.permissionService = permissionService;
        this.systemProcessor = systemProcessor;
        this.objectMapper = objectMapper;
    }

    /**
     * Processes a user message and returns the LLM's response.
     *
     * @param userMessage    the user's text message
     * @param authentication the authenticated user
     * @return the final response text
     */
    public String processMessage(String userMessage, Authentication authentication) {
        log.info("Processing chat message from user '{}': {}", authentication.getName(), userMessage);

        // Build the conversation with system prompt and user message
        List<Message> messages = new ArrayList<>();
        messages.add(buildSystemPrompt(authentication));
        messages.add(Message.user(userMessage));

        // Get tools the user is allowed to use
        List<McpToolDefinition> allowedTools = toolRegistry.getToolsForRoles(
                authentication.getAuthorities());
        List<ToolDefinition> toolDefinitions = convertToDeepSeekTools(allowedTools);

        log.debug("User '{}' has access to {} tools", authentication.getName(), allowedTools.size());

        // Conversation loop: send to DeepSeek, handle tool calls, repeat
        for (int round = 0; round < MAX_TOOL_CALL_ROUNDS; round++) {
            ChatRequest request = ChatRequest.of(
                    deepSeekClient.getModel(),
                    messages,
                    toolDefinitions.isEmpty() ? null : toolDefinitions,
                    deepSeekClient.getMaxTokens()
            );

            ChatResponse response = deepSeekClient.sendChat(request);

            if (response.choices() == null || response.choices().isEmpty()) {
                log.warn("DeepSeek returned empty choices");
                return "I'm sorry, I couldn't process your request. Please try again.";
            }

            ChatResponse.Choice choice = response.choices().get(0);
            Message assistantMessage = choice.message();

            // Check if the response is a tool call or final text
            if (assistantMessage.toolCalls() != null && !assistantMessage.toolCalls().isEmpty()) {
                log.debug("DeepSeek requested {} tool call(s) in round {}",
                        assistantMessage.toolCalls().size(), round + 1);

                // Add assistant's tool call message to history
                messages.add(assistantMessage);

                // Execute each tool call
                for (ToolCall toolCall : assistantMessage.toolCalls()) {
                    String toolName = toolCall.function().name();
                    String toolArgs = toolCall.function().arguments();

                    log.debug("Processing tool call: {} with args: {}", toolName, toolArgs);

                    // Permission check
                    if (!permissionValidator.canExecute(toolName, authentication)) {
                        String errorMsg = "Permission denied: you do not have access to execute '" + toolName + "'";
                        log.warn("Blocked tool call '{}' for user '{}'", toolName, authentication.getName());
                        messages.add(Message.tool(toolCall.id(), toolName, errorMsg));
                        continue;
                    }

                    // Module-level permission check using per-module writer role names
                    if (isModuleIdTool(toolName)) {
                        int moduleId = extractModuleId(toolArgs);
                        if (moduleId < 0) {
                            String errorMsg = "{\"success\": false, \"error\": \"Invalid or missing moduleId in arguments\"}";
                            messages.add(Message.tool(toolCall.id(), toolName, errorMsg));
                            continue;
                        }
                        McpToolAccessType mcpAccess = toolRegistry.getRequiredAccessType(toolName);
                        if (mcpAccess == null) {
                            log.warn("Unknown tool '{}' requested module-level check", toolName);
                            String errorMsg = "{\"success\": false, \"error\": \"Unknown tool: " + toolName + "\"}";
                            messages.add(Message.tool(toolCall.id(), toolName, errorMsg));
                            continue;
                        }
                        // Bridge from MCP-layer type to security-layer type
                        IHomeApiTargetAccessType requiredAccess = toSecurityAccessType(mcpAccess);
                        if (!permissionService.hasModulePermission(authentication, moduleId, requiredAccess)) {
                            log.warn("Blocked tool '{}' for module {} by user '{}' (required: {})",
                                    toolName, moduleId, authentication.getName(), requiredAccess);
                            String errorMsg = "{\"success\": false, \"error\": \"You do not have permission to " +
                                    (requiredAccess == IHomeApiTargetAccessType.WRITE ? "control" : "view") +
                                    " module " + moduleId + ".\"}";
                            messages.add(Message.tool(toolCall.id(), toolName, errorMsg));
                            continue;
                        }
                    }

                    // Execute tool directly via Spring bean (no HTTP overhead)
                    String toolResult = executeTool(toolName, toolArgs);
                    messages.add(Message.tool(toolCall.id(), toolName, toolResult));
                }

                // Continue loop — DeepSeek will process tool results
            } else {
                // Final text response
                String content = assistantMessage.content();
                log.debug("DeepSeek returned final response in round {}: {}",
                        round + 1,
                        content != null ? content.substring(0, Math.min(100, content.length())) : "null");
                return content != null ? content : "I processed your request but have no additional response.";
            }
        }

        log.warn("Reached maximum tool call rounds ({}) for user '{}'",
                MAX_TOOL_CALL_ROUNDS, authentication.getName());
        return "I've processed your request through multiple steps. Is there anything else you'd like me to help with?";
    }

    /**
     * Builds the system prompt that instructs DeepSeek how to use the available tools.
     */
    private Message buildSystemPrompt(Authentication authentication) {
        String username = authentication.getName();

        // Determine user's permission level for the prompt
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isSupervisor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERVISOR"));

        String roleDescription;
        if (isAdmin) {
            roleDescription = """
                    You have full administrator access.
                    You can view all system information, manage users, and control all devices.""";
        } else if (isSupervisor) {
            roleDescription = """
                    You have supervisor-level access.
                    You can view all system information and control supported devices.""";
        } else {
            roleDescription = """
                    Your access is limited to specific modules.
                    The table below shows which modules you can view (READ) and which you can control (READ+WRITE).""";
        }

        String moduleContext = buildModuleContext(authentication);

        return Message.system("""
                You are a helpful home automation assistant for the iHome smart home system.
                You help users monitor and control their home devices.
                
                Current user: %s
                %s
                
                ## Home Configuration
                Below is the current list of modules in this home. Each module has an ID, name,
                type (assignment), current mode, output state, and your access level.
                Use module IDs when calling tools like getModuleData or updateModuleMode.
                
                %s
                
                You have access to tools that let you interact with the home automation system.
                Use these tools to answer the user's questions about their home.
                
                Guidelines:
                - When asked about device status, use the appropriate tool to get real-time data.
                - When asked to control a device, use the update tools if you have permission.
                - If you don't have permission for an action, politely inform the user.
                - Always provide clear, concise responses about what you did or found.
                - If a tool returns an error, explain it to the user in simple terms.
                - Use Celsius for temperatures and watts/kilowatts for power.
                - Refer to modules by their name (e.g., "Garage Light") rather than just their ID.
                """.formatted(username, roleDescription, moduleContext));
    }

    /**
     * Builds a human-readable summary of modules visible to the user.
     * Each row includes the module's ID, name, type, current state, and the user's
     * access level (READ or READ+WRITE) based on per-module permissions.
     * Modules the user has no access to are excluded entirely.
     */
    private String buildModuleContext(Authentication authentication) {
        try {
            ModuleSummary[] modules = systemProcessor.getModuleList(null, null);
            if (modules == null || modules.length == 0) {
                return "(No modules configured)";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("| ID | Name | Type | Mode | Output | Access |\n");
            sb.append("|----|------|------|------|--------|--------|\n");
            boolean hasAnyModule = false;
            for (ModuleSummary m : modules) {
                // Check per-module permissions
                boolean canRead = permissionService.hasModulePermission(
                        authentication, m.getModuleId(), IHomeApiTargetAccessType.READ);
                if (!canRead) {
                    continue; // Skip modules the user can't see
                }
                hasAnyModule = true;
                boolean canWrite = permissionService.hasModulePermission(
                        authentication, m.getModuleId(), IHomeApiTargetAccessType.WRITE);
                String accessLabel = canWrite ? "READ+WRITE" : "READ";

                String type = m.getAssignment() != null ? m.getAssignment().name() : "UNKNOWN";
                String modeLabel = switch (m.getMode()) {
                    case 0 -> "AUTO";
                    case 1 -> "MANUAL";
                    case 2 -> "OFF";
                    default -> String.valueOf(m.getMode());
                };
                String outputLabel;
                if (m.isDimmableOutput()) {
                    outputLabel = m.getOutputPortState() + "%";
                } else {
                    outputLabel = m.getOutputPortState() == 1 ? "POWERED" : "OFF";
                }
                sb.append("| %d | %s | %s | %s | %s | %s |\n"
                        .formatted(m.getModuleId(), m.getName(), type, modeLabel, outputLabel, accessLabel));
            }
            if (!hasAnyModule) {
                return "(No accessible modules)";
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("Failed to build module context for system prompt: {}", e.getMessage());
            return "(Unable to load module list: " + e.getMessage() + ")";
        }
    }

    /**
     * Converts MCP tool definitions to DeepSeek-compatible tool definitions.
     */
    private List<ToolDefinition> convertToDeepSeekTools(List<McpToolDefinition> mcpTools) {
        return mcpTools.stream()
                .map(tool -> new ToolDefinition(
                        "function",
                        new ToolDefinition.Function(
                                tool.name(),
                                tool.description(),
                                tool.inputSchema()
                        )
                ))
                .toList();
    }

    /**
     * Executes a tool directly via the McpToolExecutor Spring bean.
     * No HTTP overhead — direct in-process call.
     */
    private String executeTool(String toolName, String arguments) {
        try {
            JsonNode paramsNode;
            try {
                paramsNode = arguments != null && !arguments.isEmpty()
                        ? objectMapper.readTree(arguments)
                        : objectMapper.createObjectNode();
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse tool arguments '{}', using empty params: {}", arguments, e.getMessage());
                paramsNode = objectMapper.createObjectNode();
            }

            JsonNode result = toolExecutor.execute(toolName, paramsNode);
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.error("Failed to execute tool '{}': {}", toolName, e.getMessage(), e);
            return "{\"success\": false, \"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * Bridges from the MCP-layer access type to the security-layer access type.
     * This is the only place where this translation happens — the two layers
     * use different enums ({@link McpToolAccessType} vs {@link IHomeApiTargetAccessType})
     * to avoid coupling the AI/mcp package to the security utility package.
     */
    private static IHomeApiTargetAccessType toSecurityAccessType(McpToolAccessType mcpAccess) {
        return switch (mcpAccess) {
            case PUBLIC_READ, RESTRICTED_READ, READ -> IHomeApiTargetAccessType.READ;
            case WRITE -> IHomeApiTargetAccessType.WRITE;
        };
    }

    /**
     * Returns true if the given tool takes a moduleId parameter and requires
     * per-module permission checks (both READ and WRITE).
     * The actual access type (READ vs WRITE) is resolved via
     * {@link McpToolRegistry#getRequiredAccessType(String)}.
     */
    private boolean isModuleIdTool(String toolName) {
        return "getModuleData".equals(toolName)
                || "updateModuleOutputState".equals(toolName)
                || "updateModuleMode".equals(toolName);
    }

    /**
     * Extracts the moduleId from a JSON arguments string.
     * Returns -1 if the argument is missing or unparseable.
     */
    private int extractModuleId(String arguments) {
        try {
            JsonNode node = objectMapper.readTree(arguments);
            if (node != null && node.has("moduleId") && !node.get("moduleId").isNull()) {
                return node.get("moduleId").asInt();
            }
        } catch (Exception e) {
            log.warn("Failed to extract moduleId from arguments: {}", arguments, e);
        }
        return -1;
    }
}
