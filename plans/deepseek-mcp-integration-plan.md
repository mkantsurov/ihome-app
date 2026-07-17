# DeepSeek API Integration Plan — App as Orchestrator (In-Process)

## Architecture Overview

```
┌──────────┐   JWT    ┌──────────────────┐   HTTP    ┌─────────────┐
│  UI/User │─────────→│  ChatController  │─────────→│ DeepSeek API│
│          │          │  /api/v1/chat    │          │  (cloud)    │
└──────────┘          └────────┬─────────┘          └──────┬──────┘
                               │                           │
                               │ 1. User sends message     │
                               │    (JWT in header)        │
                               │                           │
                               │ 2. Orchestrator builds    │
                               │    prompt: message +      │
                               │    tool definitions       │
                               │                           │
                               │                     3. DeepSeek responds
                               │                        with tool_call(s)
                               │                        or final answer
                               │                           │
                               │ 4. If tool_call:          │
                               │    a) Validate user       │
                               │       permissions (JWT)   │
                               │    b) Call McpToolExecutor│
                               │       (direct Spring bean)│
                               │    c) Send result back    │
                               │       to DeepSeek         │
                               │                           │
                               │                     5. DeepSeek crafts
                               │                        human-readable
                               │                        response
                               │                           │
                               │ 6. Return response        │
                               ↓                           │
                         ┌──────────────┐                 │
                         │McpToolExecutor│←────────────────┘
                         │ (Spring bean) │  direct in-process
                         └──────┬───────┘
                                │
                                │ calls existing services
                                ↓
                         ┌──────────────────┐
                         │  SystemManager   │
                         │  SystemProcessor │
                         │  StatisticProc.  │
                         └──────────────────┘
```

## Key Design Decisions

1. **Tool execution is in-process** — `ChatOrchestratorService` calls `McpToolExecutor` directly as a Spring bean. No HTTP overhead, no socket listener.
2. **Authorization happens in the Orchestrator** — the user's JWT is carried from the REST controller through the entire flow; before any tool is executed, `PermissionValidator` checks the user's roles against the tool's required permissions.
3. **DeepSeek only receives tool definitions as JSON** in the prompt and returns tool_call decisions. It never has direct access to your system.
4. **MCP-protocol-aware classes are kept for future use** — `McpJsonRpcHandler`, `JsonRpcRequest`, `JsonRpcResponse` implement the MCP JSON-RPC 2.0 protocol but are not wired into the current runtime. When you want to expose a real MCP endpoint (e.g., for Claude Desktop), wrap `McpJsonRpcHandler` behind an HTTP listener.

## Package Structure

```
src/main/java/technology/positivehome/ihome/
├── ai/
│   ├── controller/
│   │   └── ChatController.java              # POST /api/v1/chat
│   ├── deepseek/
│   │   ├── DeepSeekClient.java              # WebClient-based HTTP client
│   │   ├── DeepSeekConfig.java              # @ConfigurationProperties
│   │   ├── DeepSeekApiException.java
│   │   └── model/
│   │       ├── ChatRequest.java
│   │       ├── ChatResponse.java
│   │       ├── Message.java
│   │       ├── ToolCall.java
│   │       └── ToolDefinition.java
│   ├── mcp/
│   │   ├── McpToolDefinition.java           # Tool schema: name, description, JSON Schema, roles
│   │   ├── McpToolRegistry.java             # Registers 14 tools with role-based filtering
│   │   ├── McpToolExecutor.java             # Executes tools against SystemProcessor/StatisticProcessor
│   │   ├── McpJsonRpcHandler.java           # JSON-RPC 2.0 handler (for future MCP endpoint)
│   │   └── model/
│   │       ├── JsonRpcRequest.java          # (for future MCP endpoint)
│   │       └── JsonRpcResponse.java         # (for future MCP endpoint)
│   └── orchestrator/
│       ├── ChatOrchestratorService.java     # Core loop: prompt → DeepSeek → tools → response
│       └── PermissionValidator.java         # Role-based tool access control
└── model/runtime/chat/
    ├── ChatRequest.java                     # Incoming: { "message": "..." }
    └── ChatResponse.java                    # Outgoing: { "reply": "...", "actionsTaken": [...] }
```

## Registered Tools (14 total)

### Read-Only (any authenticated user)
| Tool | Description |
|------|-------------|
| `getSystemSummary` | Overall system summary (power, heating, security) |
| `getPowerSummary` | Detailed power consumption and supply info |
| `getHeatingSummary` | Heating system status and temperatures |
| `getModuleList` | List all modules, optionally filtered by assignment/group |
| `getModuleData` | Detailed data for a specific module by ID |
| `getTempStat` | Temperature statistics across all sensors |
| `getPowerConsumptionStat` | Power consumption statistics over time |
| `getPressureStat` | Atmospheric pressure statistics |
| `getLuminosityStat` | Luminosity/light level statistics |
| `getSystemStat` | Overall system statistics |
| `getBoilerTempStat` | Boiler temperature statistics |
| `getPowerVoltageStat` | Power voltage statistics |
| `getModuleListByGroup` | Modules belonging to a specific group |

### Admin-Only (requires ROLE_ADMIN)
| Tool | Description |
|------|-------------|
| `updateModuleMode` | Change module operation mode (AUTO/MANUAL/OFF) |
| `updateModuleOutputState` | Turn module output ON or OFF |

## Security Flow

```
1. User authenticates via /auth/login → receives JWT
2. User sends POST /api/v1/chat with JWT in Authorization header
3. JwtTokenAuthenticationProcessingFilter validates JWT → sets SecurityContext
4. @PreAuthorize("isAuthenticated()") on ChatController passes
5. ChatOrchestratorService receives the Authentication object
6. Orchestrator builds tool list filtered by user's roles:
   - User with ROLE_ADMIN → sees all 14 tools
   - User with ROLE_UNDEFINED → sees only 12 read-only tools
7. DeepSeek responds with tool_call for "updateModuleOutputState"
8. PermissionValidator checks: tool requires ROLE_ADMIN, user has ROLE_UNDEFINED → DENIED
9. Orchestrator sends error back to DeepSeek: "Permission denied"
10. DeepSeek crafts response: "I'm sorry, you don't have permission to control devices."
```

## Configuration

### application.yml
```yaml
ihome:
  ai:
    deepseek:
      api-key: ${DEEPSEEK_API_KEY:}
      base-url: https://api.deepseek.com/v1
      model: deepseek-chat
      max-tokens: 4096
```

### gradle.properties (for local development)
```properties
deepseekApiKey=sk-your-key-here
```

The `bootRun` task reads `deepseekApiKey` and passes it as `DEEPSEEK_API_KEY` env var.

## Files Created

### Main sources (17 files)
- `ai/deepseek/DeepSeekConfig.java`
- `ai/deepseek/DeepSeekClient.java`
- `ai/deepseek/DeepSeekApiException.java`
- `ai/deepseek/model/ChatRequest.java`
- `ai/deepseek/model/ChatResponse.java`
- `ai/deepseek/model/Message.java`
- `ai/deepseek/model/ToolCall.java`
- `ai/deepseek/model/ToolDefinition.java`
- `ai/mcp/McpToolDefinition.java`
- `ai/mcp/McpToolRegistry.java`
- `ai/mcp/McpToolExecutor.java`
- `ai/mcp/McpJsonRpcHandler.java`
- `ai/mcp/model/JsonRpcRequest.java`
- `ai/mcp/model/JsonRpcResponse.java`
- `ai/orchestrator/ChatOrchestratorService.java`
- `ai/orchestrator/PermissionValidator.java`
- `ai/controller/ChatController.java`
- `model/runtime/chat/ChatRequest.java`
- `model/runtime/chat/ChatResponse.java`

### Test sources (2 files)
- `ai/mcp/McpToolRegistryTest.java`
- `ai/orchestrator/PermissionValidatorTest.java`

### Modified files (3 files)
- `application.yml` — Added `ihome.ai.deepseek` config
- `ServicesConfiguration.java` — Added `@EnableConfigurationProperties` and `ai` package scan
- `build.gradle.kts` — `bootRun` passes `deepseekApiKey` as env var
- `gradle.properties` — Added `deepseekApiKey` property

## Future: Exposing a Real MCP Endpoint

When you want external MCP clients (Claude Desktop, Continue.dev, etc.) to connect:

1. Create a new `McpServer.java` that wraps `McpJsonRpcHandler` behind an HTTP listener
2. Bind to `127.0.0.1:8081` (or a configurable port)
3. The JSON-RPC handler already implements `initialize`, `tools/list`, `tools/call`
4. Add authentication to the MCP endpoint (API key or JWT)

The protocol layer is already built — you just need to add the transport.
