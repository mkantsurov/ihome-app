# GitHub Copilot Instructions for ihome-app

## Build System

- Use **Gradle** (not `gradlew`) for all build commands.
- The project uses Gradle with Kotlin DSL (`build.gradle.kts`).
- Gradle wrapper is configured but not committed — invoke `gradle` directly.

## Project Overview

- **Spring Boot 3.5.14** web application for home automation (I-Home).
- Main class: `technology.positivehome.ihome.ServerApplication`
- Java version: **25** (source/target compatibility)
- Package: `technology.positivehome.ihome`

## Running the Application

```bash
# Run with debug and custom config
gradle bootRun -PtestSpringConfLocation=src/test/resources/test-spring-conf.yaml
```

## Running Tests

```bash
# Run unit tests (filtered to technology.positivehome.ihome.server.processor.*)
gradle test -PtestSpringConfLocation=src/test/resources/test-spring-conf.yaml

# Run integration tests
gradle integrationTest -PtestSpringConfLocation=src/test/resources/test-spring-conf.yaml
```

## Building

```bash
# Build the bootable JAR
gradle bootJar -PtestSpringConfLocation=src/test/resources/test-spring-conf.yaml

# Build Docker image via Jib
gradle jib -PtestSpringConfLocation=src/test/resources/test-spring-conf.yaml -PdockerRepository=ghcr.io
```

## Project Structure

```
src/
├── main/
│   ├── java/technology/positivehome/ihome/
│   │   ├── configuration/       # Spring @Configuration classes
│   │   ├── domain/              # Domain models (runtime, shared, constant)
│   │   ├── security/            # JWT auth, security config
│   │   └── server/              # Controllers, services, persistence, processors
│   └── resources/
├── test/
│   ├── java/technology/positivehome/ihome/server/
│   │   ├── processor/           # Unit tests (processor package)
│   │   └── service/core/controller/  # Controller tests
│   └── resources/
│       └── test-spring-conf.yaml     # Test configuration
└── integrationTest/
    ├── java/
    └── resources/
```

## Key Dependencies

- **Spring Boot**: Web, Security, JDBC, WebSocket, WebFlux, Validation, Actuator
- **Database**: PostgreSQL 42.7.11, HikariCP 2.7.4, Flyway 7.5.2
- **Security**: JWT (jjwt 0.9.0), JAXB API 2.3.0
- **Testing**: JUnit 5, Mockito 5.12.0, Spring REST Docs, JSON Path
- **Other**: Guava 33.0.0, Apache Commons Lang 3.11, HttpClient 4.5.13, Commons IO 2.14.0

## Coding Conventions

- Java 25 features are available.
- Use `@Qualifier` annotations for dependency injection disambiguation.
- `@Bean` methods should **not** have `@Autowired` — Spring auto-wires `@Bean` method parameters automatically.
- Use `NamedParameterJdbcTemplate` for database access.
- Row mappers implement custom mapping from `ResultSet` to domain entities.
- Repositories follow a custom pattern (`GenericIHomeRepository`, `AuditableIHomeRepository`).
- Controllers use `@RestController` and return domain objects.
- Security uses JWT token-based authentication with Ajax login fallback.

## Test Configuration

Test configuration is in `src/test/resources/test-spring-conf.yaml` and is passed via the `testSpringConfLocation` Gradle property:

```yaml
ihome:
  app:
    url: localhost:4200/web
    login-page: /ihome-login.jsp
    auth-page: /j_security_check
    emulation-mode: true
spring:
  datasource:
    url: jdbc:postgresql://192.168.88.241:5432/ihome
    username: ihome
    password: 9jWvIA9gya94iPT
  flyway:
    url: jdbc:postgresql://192.168.88.241:5432/ihome
    user: ihome
    password: 9jWvIA9gya94iPT
```

## Gradle Properties

Key properties defined in `gradle.properties`:
- `testSpringConfLocation` — path to test YAML config
- `dockerRepository` — Docker registry (default: `ghcr.io`)
- `debugEnabled=true`
- `deepseekApiKey` — DeepSeek API key (passed as `DEEPSEEK_API_KEY` env var by `bootRun`)

## AI Integration (DeepSeek + MCP)

### Package Structure
```
src/main/java/technology/positivehome/ihome/ai/
├── controller/ChatController.java           # POST /api/v1/chat (JWT auth)
├── deepseek/
│   ├── DeepSeekClient.java                  # WebClient-based HTTP client
│   ├── DeepSeekConfig.java                  # @ConfigurationProperties (ihome.ai.deepseek)
│   ├── DeepSeekApiException.java
│   └── model/{ChatRequest,ChatResponse,Message,ToolCall,ToolDefinition}.java
├── mcp/
│   ├── McpToolDefinition.java               # Tool schema: name, description, JSON Schema, required roles, optional resultTransformer
│   ├── McpToolRegistry.java                 # Registers 14 tools, role-based filtering, scaleChartPointValues helper
│   ├── McpToolExecutor.java                 # Executes tools against SystemProcessor/StatisticProcessor, applies resultTransformer
│   ├── McpJsonRpcHandler.java               # JSON-RPC 2.0 handler (for future MCP endpoint)
│   └── model/{JsonRpcRequest,JsonRpcResponse}.java
└── orchestrator/
    ├── ChatOrchestratorService.java          # Core loop: prompt → DeepSeek → tools → response
    └── PermissionValidator.java             # Role-based tool access control
```

### Architecture Flow
```
User (JWT) → ChatController → ChatOrchestratorService → DeepSeek API (cloud)
                                │                            │
                                │ tool_call decision        │
                                ↓                            │
                          McpToolExecutor (direct call) ←───┘
                                │
                                ↓
                          SystemProcessor / StatisticProcessor
```

### Dynamic System Prompt (Home Context)
- `ChatOrchestratorService.buildSystemPrompt()` injects a live module list into the system prompt via `buildModuleContext()`
- `buildModuleContext()` calls `systemProcessor.getModuleList(null, null)` and formats modules as a markdown table: ID, Name, Type (assignment), Mode (AUTO/MANUAL/OFF), Output (ON/OFF or dimmer %)
- This gives the LLM real-time knowledge of the home layout — module names, IDs, types, and current states
- The LLM can then refer to modules by name ("Garage Light") and use correct module IDs in tool calls
- Falls back gracefully to "(No modules configured)" or "(Unable to load module list: ...)" on errors

### Tool Registration Pattern
- Tools are registered in `McpToolRegistry.registerTools()` via `@PostConstruct`
- Each tool has: `name`, `description`, `inputSchema` (JSON Schema), `requiredRoles` (Set<Role>), and an optional `resultTransformer` (Function<JsonNode, JsonNode>)
- 12 read-only tools (any authenticated user) + 2 admin tools (ROLE_ADMIN only)
- Admin tools: `updateModuleMode`, `updateModuleOutputState`

### Result Transformer Pattern
- `McpToolDefinition` accepts an optional `Function<JsonNode, JsonNode> resultTransformer` (5th record component, defaults to `null`)
- The 4-arg constructor `McpToolDefinition(name, description, inputSchema, requiredRoles)` delegates to the 5-arg canonical constructor with `null` transformer — backward compatible
- `McpToolExecutor` looks up the tool definition after building the JSON response and applies the transformer if present
- Use `McpToolRegistry.scaleChartPointValues(double factor)` to create a transformer that recursively walks the JSON tree, finds `ChartPointInfo` nodes (objects with both `dt` and `value` fields where `value` is an integer), scales `value` by the factor, and preserves the raw value as `valueRaw`
- Tools with `scaleChartPointValues(0.01)`: `getTempStat`, `getPressureStat`, `getBoilerTempStat`, `getPowerVoltageStat` — converts raw hundredths (e.g., `2322`) to decimal (e.g., `23.22`)
- Tools without transformers: `getSystemStat`, `getPowerConsumptionStat`, `getLuminosityStat` — values pass through unchanged
- When adding a new tool that returns `ChartPointInfo` with scaled integers, use `scaleChartPointValues(factor)` in the `register()` call

### Permission Model (Two-Layer Defense)
1. **Layer 1 — Tool list filtering**: `McpToolRegistry.getToolsForRoles()` filters tools before sending to DeepSeek. Non-admin users never see admin tools.
2. **Layer 2 — Execution guard**: `PermissionValidator.canExecute()` blocks any unexpected tool call at runtime.

### Configuration
```yaml
# application.yml
ihome.ai.deepseek:
  api-key: ${DEEPSEEK_API_KEY:}
  base-url: https://api.deepseek.com/v1
  model: deepseek-chat
  max-tokens: 4096
```

### Key Classes
- `ChatOrchestratorService` — main orchestrator; builds system prompt with live module context, sends to DeepSeek, handles tool_call loop (max 5 rounds), returns final text
- `McpToolExecutor` — switch-based dispatch mapping tool names to `SystemProcessor`/`StatisticProcessor` methods
- `DeepSeekClient` — uses Spring `WebClient` (already available via webflux starter), no additional dependencies needed
- `DeepSeekConfig` — Java record with `@ConfigurationProperties(prefix = "ihome.ai.deepseek")`

### Test Patterns
- AI tests go in `src/test/java/technology/positivehome/ihome/ai/`
- Use `@ExtendWith(MockitoExtension.class)` with `@Mock` for dependencies
- `McpToolRegistry` can be tested without Spring by calling `registerTools()` manually
