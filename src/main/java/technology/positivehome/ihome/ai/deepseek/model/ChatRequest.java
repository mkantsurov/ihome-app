package technology.positivehome.ihome.ai.deepseek.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a request to the DeepSeek chat completions API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatRequest(
        String model,
        List<Message> messages,
        List<ToolDefinition> tools,
        @JsonProperty("tool_choice") String toolChoice,
        @JsonProperty("max_tokens") int maxTokens,
        double temperature
) {
    public static ChatRequest of(String model, List<Message> messages, List<ToolDefinition> tools, int maxTokens) {
        return new ChatRequest(model, messages, tools, "auto", maxTokens, 0.1);
    }
}
