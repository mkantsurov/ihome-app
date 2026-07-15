package technology.positivehome.ihome.ai.mcp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * JSON-RPC 2.0 response object.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JsonRpcResponse(
        @JsonProperty("jsonrpc") String jsonrpc,
        JsonNode result,
        JsonRpcError error,
        String id
) {
    public JsonRpcResponse {
        if (jsonrpc == null) {
            jsonrpc = "2.0";
        }
    }

    public static JsonRpcResponse success(String id, JsonNode result) {
        return new JsonRpcResponse("2.0", result, null, id);
    }

    public static JsonRpcResponse error(String id, int code, String message) {
        return new JsonRpcResponse("2.0", null, new JsonRpcError(code, message), id);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record JsonRpcError(
            int code,
            String message
    ) {}
}
