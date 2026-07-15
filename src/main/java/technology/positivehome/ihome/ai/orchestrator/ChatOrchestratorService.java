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
import technology.positivehome.ihome.ai.mcp.McpToolDefinition;
import technology.positivehome.ihome.ai.mcp.McpToolExecutor;
import technology.positivehome.ihome.ai.mcp.McpToolRegistry;
import technology.positivehome.ihome.model.runtime.module.ModuleSummary;
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
    private final SystemProcessor systemProcessor;
    private final ObjectMapper objectMapper;

    public ChatOrchestratorService(DeepSeekClient deepSeekClient,
                                   McpToolRegistry toolRegistry,
                                   McpToolExecutor toolExecutor,
                                   PermissionValidator permissionValidator,
                                   SystemProcessor systemProcessor,
                                   ObjectMapper objectMapper) {
        this.deepSeekClient = deepSeekClient;
        this.toolRegistry = toolRegistry;
        this.toolExecutor = toolExecutor;
        this.permissionValidator = permissionValidator;
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
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String roleDescription = isAdmin
                ? "You have full administrator access to control all home automation devices."
                : "You have read-only access. You can view status and statistics but cannot control devices.";

        String moduleContext = buildModuleContext();

        return Message.system("""
                You are a helpful home automation assistant for the iHome smart home system.
                You help users monitor and control their home devices.
                
                Current user: %s
                %s
                
                ## Home Configuration
                Below is the current list of modules in this home. Each module has an ID, name,
                type (assignment), current mode, and output state. Use module IDs when calling tools
                like getModuleData or updateModuleMode.
                
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
     * Builds a human-readable summary of all modules in the system.
     * This is injected into the system prompt so the LLM understands the home layout.
     */
    private String buildModuleContext() {
        try {
            ModuleSummary[] modules = systemProcessor.getModuleList(null, null);
            if (modules == null || modules.length == 0) {
                return "(No modules configured)";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("| ID | Name | Type | Mode | Output |\n");
            sb.append("|----|------|------|------|--------|\n");
            for (ModuleSummary m : modules) {
                String type = m.getAssignment() != null ? m.getAssignment().name() : "UNKNOWN";
                String modeLabel = switch (m.getMode()) {
                    case 0 -> "AUTO";
                    case 1 -> "MANUAL";
                    case 2 -> "OFF";
                    default -> String.valueOf(m.getMode());
                };
                String outputLabel = m.isDimmableOutput()
                        ? m.getOutputPortState() + "%"
                        : (m.getOutputPortState() == 1 ? "ON" : "OFF");
                sb.append("| %d | %s | %s | %s | %s |\n"
                        .formatted(m.getModuleId(), m.getName(), type, modeLabel, outputLabel));
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
}
