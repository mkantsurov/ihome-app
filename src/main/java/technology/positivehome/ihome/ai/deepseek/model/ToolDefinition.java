package technology.positivehome.ihome.ai.deepseek.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a tool/function definition sent to the LLM.
 * Follows the OpenAI function-calling schema (compatible with DeepSeek).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ToolDefinition(
        String type,
        Function function
) {
    public ToolDefinition {
        if (type == null) {
            type = "function";
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Function(
            String name,
            String description,
            JsonNode parameters
    ) {}
}
