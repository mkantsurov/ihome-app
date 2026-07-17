package technology.positivehome.ihome.ai.mcp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * JSON-RPC 2.0 request object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JsonRpcRequest(
        @JsonProperty("jsonrpc") String jsonrpc,
        String method,
        JsonNode params,
        String id
) {
    public JsonRpcRequest {
        if (jsonrpc == null) {
            jsonrpc = "2.0";
        }
    }
}
