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
import technology.positivehome.ihome.model.runtime.chat.IHomeChatRequest;
import technology.positivehome.ihome.model.runtime.module.ModuleSummary;
import technology.positivehome.ihome.security.model.user.Role;
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
     * Processes a chat request containing the full conversation history and returns the LLM's response.
     * The server prepends a fresh system prompt (with current permissions and module state)
     * to the conversation history provided by the UI.
     *
     * @param conversationHistory the full conversation history from the UI ({@code role: user/ai, text: ...})
     * @param authentication      the authenticated user
     * @return the final response text
     */
    public String processMessage(List<IHomeChatRequest.ChatMessage> conversationHistory, Authentication authentication) {
        IHomeChatRequest.ChatMessage lastMessage = conversationHistory.get(conversationHistory.size() - 1);
        log.info("Processing chat message from user '{}': {}", authentication.getName(), lastMessage.text());

        // Build the conversation with a fresh system prompt + UI-provided history
        List<Message> messages = new ArrayList<>();
        messages.add(buildSystemPrompt(authentication));

        // Inject the conversation history from the UI (excluding the last user message,
        // which is added below to keep it at the end of the conversation)
        for (int i = 0; i < conversationHistory.size() - 1; i++) {
            IHomeChatRequest.ChatMessage historyMsg = conversationHistory.get(i);
            Message deepSeekMsg = switch (historyMsg.role()) {
                case "user" -> Message.user(historyMsg.text());
                case "ai" -> Message.assistant(historyMsg.text());
                default -> {
                    log.warn("Skipping unknown role in conversation history: {}", historyMsg.role());
                    yield null;
                }
            };
            if (deepSeekMsg != null) {
                messages.add(deepSeekMsg);
            }
        }

        // Add the latest user message
        messages.add(Message.user(lastMessage.text()));

        // Get tools the user is allowed to use
        List<McpToolDefinition> allowedTools = toolRegistry.getToolsForRoles(authentication);
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

            ChatResponse.Choice choice = response.choices().getFirst();
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

                    // Module-level permission check for tools that target a specific module
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
                        IHomeApiTargetAccessType requiredAccess = (mcpAccess == McpToolAccessType.WRITE || mcpAccess == McpToolAccessType.ADMIN_ONLY)
                                ? IHomeApiTargetAccessType.WRITE
                                : IHomeApiTargetAccessType.READ;
                        if (!permissionService.hasModulePermission(authentication, moduleId, requiredAccess)) {
                            log.warn("Blocked tool '{}' for module {} by user '{}'",
                                    toolName, moduleId, authentication.getName());
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
                .anyMatch(a -> a.getAuthority().equals(Role.ADMIN.authority()));
        boolean isSupervisor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.SUPERVISOR.authority()));

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
            // For users without admin or supervisor (e.g., AUTHORIZED_GUEST, CHILDREN_ROOM*_MANAGER, UNDEFINED),
            // the module context below will provide (or omit) the module table based on their actual tool access.
            roleDescription = """
                    Your access level is read-only on select system information.
                    See the module table below for your specific module permissions.""";        }

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
                
                ## Module Mode Reference
                Each module has an operation mode shown in the table above:
                
                - **UNDEFINED mode**: The module's operation mode has not been explicitly set.
                  Treat this as the initial/unconfigured state.
                
                - **MANUAL mode**: The system does NOT apply any automatic actions to this module.
                  The module's output state is controlled only by direct user commands (via
                  tool calls like updateModuleOutputState). If the module turns on or off,
                  it was because you or the user explicitly requested it — not the system.
                
                - **AUTO mode**: The system CAN enable or disable this module automatically,
                  depending on hardcoded conditions in the project (e.g., motion sensor
                  triggers a light, temperature thresholds activate heating, time-based
                  schedules for ventilation). While the system manages the module, you can
                  still call getModuleData to read its current state or (if your access level
                  allows) updateModuleOutputState to override it temporarily. Be aware that
                  in AUTO mode, the system may later override the user's manual change based
                  on its conditions.
                
                Important: Module mode can only be changed by administrators via the
                updateModuleMode tool. Regular users can only see the mode and understand
                whether a module is under automatic or manual control.
                
                ## Startup Mode Reference
                Each module also has a **startup mode** shown in the "Startup" column of the
                table above (ENABLED or DISABLED). This determines what happens when the
                iHome system restarts:
                
                - **ENABLED**: On system restart, the module will be automatically turned on
                  (output = POWERED). This is useful for devices that should always resume
                  operation after a power outage or system reboot (e.g., heating pumps,
                  ventilation, refrigerators).
                  Some modules additionally enforce this: if they are in AUTO mode and the
                  startup mode is ENABLED, the system will re-enable the output if it was
                  manually turned off (checked periodically via cron tasks).
                
                - **DISABLED**: On system restart, the module will stay off (output = OFF).
                  The user must explicitly turn it on after a reboot. This is safer for
                  devices that should not resume automatically after a power event (e.g.,
                  garage lights, non-critical loads).
                
                You can update a module's startup mode via the updateModuleProps tool
                (controlled via the `enabledOnStartup` boolean field). Note:
                - This change persists across restarts — it is saved to the database.
                - It takes effect immediately AND on the next system restart.
                - Changing startup mode does NOT change the current output state; it only
                  changes behavior on the next restart (or cron re-enforcement in AUTO mode).
                
                You have access to tools that let you interact with the home automation system.
                Use these tools to answer the user's questions about their home.
                
                ## Critical: Device Access Scope
                The Home Configuration table above is the COMPLETE AND AUTHORITATIVE list of devices
                that the current user can interact with. Only module IDs shown in that table are
                accessible to this user.
                
                Guidelines:
                - When asked about device status, use the appropriate tool to get real-time data.
                - When asked to control a device, use the update tools if you have permission.
                - If you don't have permission for an action, politely inform the user.
                - Always provide clear, concise responses about what you did or found.
                - If a tool returns an error, explain it to the user in simple terms.
                - Use Celsius for temperatures and watts/kilowatts for power.
                - Refer to modules by their name (e.g., "Garage Light") rather than just their ID.
                - ONLY call tools (getModuleData, updateModuleOutputState, updateModuleMode, etc.)
                  using module IDs from the Home Configuration table above. Never attempt to
                  query or control a module ID that is not listed — the user does not have access
                  to it.
                - When answering about devices or taking actions, always scope your response to
                  the modules listed in the Home Configuration table. If the user asks about a
                  device that is not in that table, explain that it is not available to them
                  rather than returning empty/generic results or attempting tool calls for it.
                """.formatted(username, roleDescription, moduleContext));
    }

    /**
     * Builds a human-readable summary of modules visible to the user.
     * Each row includes the module's ID, name, type, current state, and the user's
     * access level (READ or READ+WRITE) based on per-module permissions.
     * Modules the user has no access to are excluded entirely.
     *
     * <p>If the user does not have access to the {@code getModuleList} tool
     * (e.g., AUTHORIZED_GUEST), this returns a message indicating that module
     * details are not available, rather than leaking a full module table that
     * the user has no tools to query.</p>
     */
    private String buildModuleContext(Authentication authentication) {
        // Gate on whether the user can access any module-list tool.
        // If they can't even see getModuleList (e.g., AUTHORIZED_GUEST), don't leak the table.
        if (!toolRegistry.canExecute("getModuleList", authentication)) {
            return "(Module details are not available at your access level. "
                    + "See the tools listed above for what you can query.)";
        }

        try {
            ModuleSummary[] modules = systemProcessor.getModuleList(null, null);
            if (modules == null || modules.length == 0) {
                return "(No modules configured)";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("| ID | Name | Type | Mode | Startup | Output | Access |\n");
            sb.append("|----|------|------|------|---------|--------|--------|\n");
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
                    case 0 -> "UNDEFINED";
                    case 1 -> "MANUAL";
                    case 2 -> "AUTO";
                    default -> String.valueOf(m.getMode());
                };
                String outputLabel;
                if (m.isDimmableOutput()) {
                    outputLabel = m.getOutputPortState() + "%";
                } else {
                    outputLabel = m.getOutputPortState() == 1 ? "POWERED" : "OFF";
                }
                String startupLabel = m.getStartupMode() == 1 ? "ENABLED" : "DISABLED";
                sb.append("| %d | %s | %s | %s | %s | %s | %s |\n"
                        .formatted(m.getModuleId(), m.getName(), type, modeLabel, startupLabel, outputLabel, accessLabel));
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
