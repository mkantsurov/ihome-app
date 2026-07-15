package technology.positivehome.ihome.ai.deepseek.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a message in the DeepSeek chat conversation.
 * Roles: system, user, assistant, tool.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Message(
        String role,
        String content,
        @JsonProperty("tool_calls") List<ToolCall> toolCalls,
        @JsonProperty("tool_call_id") String toolCallId,
        String name
) {
    public static Message system(String content) {
        return new Message("system", content, null, null, null);
    }

    public static Message user(String content) {
        return new Message("user", content, null, null, null);
    }

    public static Message assistant(String content) {
        return new Message("assistant", content, null, null, null);
    }

    public static Message assistantWithToolCalls(List<ToolCall> toolCalls) {
        return new Message("assistant", null, toolCalls, null, null);
    }

    public static Message tool(String toolCallId, String name, String content) {
        return new Message("tool", content, null, toolCallId, name);
    }
}
