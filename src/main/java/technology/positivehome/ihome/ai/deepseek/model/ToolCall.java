package technology.positivehome.ihome.ai.deepseek.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a tool call requested by the LLM.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ToolCall(
        String id,
        String type,
        Function function
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Function(
            String name,
            String arguments
    ) {}
}
