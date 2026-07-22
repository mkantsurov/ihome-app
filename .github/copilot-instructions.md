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

## Naming Convention: Entity vs Entry

This project follows a strict naming convention in the persistence layer:

- **`*Entity`** — a persistence model class (record or POJO) that maps directly to a database table/row. Entities contain all raw DB columns, including foreign keys (e.g., `groupId`). Entities live in `server/persistence/model/` or in the relevant domain package. They are used **internally** by repositories and row mappers, and are **never** returned directly to controllers or services. Examples: `UserEntity`, `ModuleConfigEntity`.

- **`*Entry`** — a plain POJO (Java object) used as a **DTO** (data transfer object). Entries contain domain-level fields (no raw FKs) and are returned by repositories to the rest of the application. They can be arguments to or return values of repository methods. Examples: `ModuleConfigEntry`, `ModuleConfigElementEntry`, `ModulePropertyValue`, `ModuleGroupEntry`.

- **Conversion rule**: Repositories convert `*Entity` → `*Entry` at their boundary. The entity is never exposed outside the persistence layer. The `*Entry` (DTO) is what services and controllers interact with.

- **Package location**:
  - New entities (records) should go in `server/persistence/model/` (e.g., `UserEntity`).
  - Older entities may still live in `domain/runtime/module/` (e.g., `ModuleConfigEntity`) — new code should follow the `server/persistence/model/` convention.
  - All entries (DTOs) remain in `domain/runtime/module/` or their respective domain packages.

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
- Each tool has: `name`, `description`, `inputSchema` (JSON Schema), `accessType` (McpToolAccessType), and an optional `resultTransformer` (Function<JsonNode, JsonNode>)
- **16 tools total**: 5 PUBLIC_READ, 8 RESTRICTED_READ, 1 WRITE, 1 ADMIN_ONLY

### Tool Access Types (McpToolAccessType)
Tools are categorized into four access types that control which roles can see and execute them:

| Access Type | Roles | Count | Tools |
|-------------|-------|-------|-------|
| **PUBLIC_READ** | Any authenticated user (including `AUTHORIZED_GUEST`) | 5 | `getTempStat`, `getPressureStat`, `getPowerSummary`, `getPowerConsumptionStat`, `getPowerVoltageStat` |
| **RESTRICTED_READ** | ADMIN, SUPERVISOR, ROLE_UNDEFINED (but NOT AUTHORIZED_GUEST) | 8 | `getSystemSummary`, `getHeatingSummary`, `getLuminosityStat`, `getSystemStat`, `getModuleList`, `getModuleData`, `getModuleListByGroup`, `getBoilerTempStat` |
| **WRITE** | ADMIN, SUPERVISOR | 1 | `updateModuleOutputState` |
| **ADMIN_ONLY** | ADMIN only (not even SUPERVISOR) | 1 | `updateModuleMode` |

### Mapping PUBLIC_READ tools to GuestController
PUBLIC_READ tools are those whose **data categories** overlap with the GuestController's endpoints. Even if the MCP tool returns more data than the guest controller endpoint (e.g. `getTempStat` returns all sensors including indoor, but also outdoor), the category match is the deciding factor.

| GuestController Endpoint | Data Provided | Related PUBLIC_READ MCP Tool(s) |
|--------------------------|---------------|----------------------------------|
| `/guest-api/v1/stats/outdoor-temp-stat` | Outdoor temperature & humidity (`OutDoorTempStatInfo`) | `getTempStat` (branches indoorSf, indoorGf, outdoor, garage) |
| `/guest-api/v1/stats/pressure-stat` | Pressure (`PressureStatInfo`) | `getPressureStat` (exact method match) |
| `/guest-api/v1/stats/power-stat` | External power voltage (`PowerVoltageExtStatInfo`) | `getPowerVoltageStat`, `getPowerConsumptionStat` (contain external power data) |
| `/guest-api/v1/stats/power-summary` | External power summary (`ExternalPowerSummaryInfo`) | `getPowerSummary` (contains external power supply info) |

### Role-Based Tool Visibility
- **AUTHORIZED_GUEST** — sees only PUBLIC_READ tools (5 tools)
- **ROLE_UNDEFINED** — sees PUBLIC_READ + RESTRICTED_READ but NOT WRITE or ADMIN_ONLY tools (13 tools)
- **SUPERVISOR** — sees PUBLIC_READ + RESTRICTED_READ + WRITE tools (14 tools); write execution is further restricted by `PermissionService.canControlModuleAssignment()` (can only control `LIGHT_CONTROL`, `EXT_LIGHT_CONTROL`, `GATE_CONTROL`); ADMIN_ONLY tools are invisible
- **ADMIN** — sees ALL 16 tools; no execution restrictions on write tools

Write tools are visible to both ADMIN and SUPERVISOR roles via `McpToolRegistry`, but at execution time `PermissionService.canControlModuleAssignment()` further restricts which module types SUPERVISOR can actually control.

### Result Transformer Pattern
- `McpToolDefinition` accepts an optional `Function<JsonNode, JsonNode> resultTransformer` (5th record component, defaults to `null`)
- The 4-arg constructor `McpToolDefinition(name, description, inputSchema, requiredRoles)` delegates to the 5-arg canonical constructor with `null` transformer — backward compatible
- `McpToolExecutor` looks up the tool definition after building the JSON response and applies the transformer if present
- Use `McpToolRegistry.scaleChartPointValues(double factor)` to create a transformer that recursively walks the JSON tree, finds `ChartPointInfo` nodes (objects with both `dt` and `value` fields where `value` is an integer), scales `value` by the factor, and preserves the raw value as `valueRaw`
- Tools with `scaleChartPointValues(0.01)`: `getTempStat`, `getPressureStat`, `getBoilerTempStat`, `getPowerVoltageStat` — converts raw hundredths (e.g., `2322`) to decimal (e.g., `23.22`)
- Tools without transformers: `getSystemStat`, `getPowerConsumptionStat`, `getLuminosityStat` — values pass through unchanged
- When adding a new tool that returns `ChartPointInfo` with scaled integers, use `scaleChartPointValues(factor)` in the `register()` call

### Permission Model (Three-Layer Defense)
1. **Layer 1 — Tool list filtering**: `McpToolRegistry.getToolsForRoles()` filters tools before sending to DeepSeek. Non-admin users never see admin tools.
2. **Layer 2 — Execution guard**: `PermissionValidator.canExecute()` blocks any unexpected tool call at runtime — checks whether the user's role is allowed to invoke the tool at all.
3. **Layer 3 — Module-level permission guard**: `ChatOrchestratorService` enforces per-module granularity for tools that target a specific module ID (`getModuleData`, `updateModuleOutputState`, `updateModuleMode`). Before executing, it resolves the required access type (READ or WRITE) from `McpToolRegistry.getRequiredAccessType()` and calls `PermissionService.hasModulePermission()`. Note that `updateModuleMode` is `ADMIN_ONLY`, so it only reaches this guard if the user is ADMIN, and ADMIN has WRITE permission on all modules.

### Module-Type Permission Rules
- **ADMIN** — can control **any** module type
- **SUPERVISOR** — can only control: `LIGHT_CONTROL`, `EXT_LIGHT_CONTROL`, `GATE_CONTROL`
- **All other roles** — cannot control any modules

The whitelist is defined in `PermissionService.SUPERVISOR_CONTROLLABLE_ASSIGNMENTS` (a `Set<String>` of enum names). When adding new controllable module types, add them there.

### Module-Type Check in ChatOrchestratorService
- `isModuleIdTool(toolName)` — returns `true` for `getModuleData`, `updateModuleOutputState`, and `updateModuleMode`; these tools need module-level permission checking. Read tools (like `getModuleData` with `RESTRICTED_READ`) check READ permission; write tools use the same method and check WRITE permission. The actual access type (READ vs WRITE) is resolved via `McpToolRegistry.getRequiredAccessType(toolName)`.
- `extractModuleId(arguments)` — parses `moduleId` from the tool's JSON arguments string
- The check flow: parse `moduleId` → `permissionService.hasModulePermission(auth, moduleId, requiredAccess)` → deny with JSON error if not permitted
- If the module cannot be resolved (e.g. deleted between the system prompt and the tool call), the action is denied with a safe default message

### Dynamic System Prompt (Home Context + Permissions)
- `ChatOrchestratorService.buildSystemPrompt()` injects a live module list into the system prompt via `buildModuleContext()`
- `buildModuleContext()` calls `systemProcessor.getModuleList(null, null)` and formats modules as a markdown table: ID, Name, Type (assignment), Mode (AUTO/MANUAL/OFF), Startup (ENABLED/DISABLED), Output (ON/OFF or dimmer %), Access (READ/READ+WRITE)
- The role description section is now **dynamic**: `permissionService.getControllableModulesDescription(authentication)` returns a human-readable string describing which module types the user can control (e.g. `"lights, external lights, garage doors, sliding gates"` for SUPERVISOR, `"all module types (lights, gates, heating, power supply, ventilation, etc.)"` for ADMIN)
- Falls back gracefully to "(No modules configured)" or "(Unable to load module list: ...)" on errors

### ModuleStartupMode in the System Prompt
- The system prompt now includes a **Startup Mode Reference** section that explains `ModuleStartupMode` (`ENABLED` / `DISABLED`) to the LLM
- **ENABLED**: on system restart, module turns on automatically; some modules (e.g., `SolarSystemPumpPowerControlModule`) re-enforce this via cron tasks in AUTO mode
- **DISABLED**: on system restart, module stays off
- The "Startup" column in the module table shows the current startup mode for each module (derived from `ModuleSummary.getStartupMode()` — ordinal 0 = DISABLED, 1 = ENABLED)
- Users can change startup mode via `updateModuleProps` tool (the `enabledOnStartup` boolean field), which persists to DB and takes effect on next restart

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
- `ChatOrchestratorService` — main orchestrator; builds system prompt with live module context and dynamic role permissions, sends to DeepSeek, handles tool_call loop (max 5 rounds), enforces module-assignment-level permission check before executing write tools
- `PermissionValidator` — first-line defense; checks if the user's role can invoke a tool at all
- `PermissionService` — second-line defense; checks if the user's role can control a specific `ModuleAssignment` type (e.g. supervisor can control lights/gates but not heating)
- `McpToolExecutor` — switch-based dispatch mapping tool names to `SystemProcessor`/`StatisticProcessor` methods
- `DeepSeekClient` — uses Spring `WebClient` (already available via webflux starter), no additional dependencies needed
- `DeepSeekConfig` — Java record with `@ConfigurationProperties(prefix = "ihome.ai.deepseek")`

### Test Patterns
- AI tests go in `src/test/java/technology/positivehome/ihome/ai/`
- Use `@ExtendWith(MockitoExtension.class)` with `@Mock` for dependencies
- `McpToolRegistry` can be tested without Spring by calling `registerTools()` manually
