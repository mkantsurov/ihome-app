# Copilot Instructions for I-Home App

## Project Overview

I-Home is a Spring Boot-based home automation backend server. It manages IoT controllers (Megad, DR404), sensors (temperature, humidity, luminosity, pressure, power meters), and automation modules (relay/dimmer-based power control, heating, ventilation, solar, etc.). The system uses PostgreSQL for persistence, Flyway for database migrations, and JWT-based authentication.

## Technology Stack

- **Language**: Java 25
- **Framework**: Spring Boot 3.1.4
- **Build System**: Gradle (Kotlin DSL)
- **Database**: PostgreSQL with Flyway migrations
- **Containerization**: Jib (Google Cloud Tools) — builds Docker images without Docker daemon
- **Security**: JWT-based authentication (access + refresh tokens), Spring Security
- **Testing**: JUnit 5, Mockito, Spring REST Docs, JSON Path
- **Persistence**: JDBC with HikariCP connection pool (no JPA/Hibernate)
- **Web**: REST APIs, WebSocket support, WebClient (WebFlux)

## Project Structure

```
src/
├── main/
│   ├── java/technology/positivehome/ihome/
│   │   ├── ServerApplication.java          # Main entry point
│   │   ├── configuration/                   # Spring configuration classes
│   │   │   ├── MvcConfig.java
│   │   │   ├── WebSocketConfig.java
│   │   │   ├── WebSecurityConfig.java
│   │   │   ├── MethodSecurityConfig.java
│   │   │   ├── JwtSettings.java
│   │   │   ├── PasswordEncoderConfig.java
│   │   │   ├── ServicesConfiguration.java
│   │   │   ├── PersistenceConfiguration.java
│   │   │   └── ...
│   │   ├── domain/                          # Domain models
│   │   │   ├── constant/                    # Enums and constants
│   │   │   ├── runtime/                     # Runtime domain objects
│   │   │   │   ├── event/                   # Event/audit log entities
│   │   │   │   ├── module/                  # Module-related models
│   │   │   │   ├── sensor/                  # Sensor data models
│   │   │   │   ├── controller/              # Controller config models
│   │   │   │   └── exception/               # Domain exceptions
│   │   │   └── shared/                      # Shared stat info DTOs
│   │   ├── server/
│   │   │   ├── controller/                  # REST controllers
│   │   │   ├── model/                       # Request/response models
│   │   │   │   └── command/                 # IoT command models
│   │   │   ├── persistence/                 # Data access layer
│   │   │   │   ├── model/                   # DB entity models
│   │   │   │   ├── mapper/                  # Row mappers
│   │   │   │   └── repository/              # Repository interfaces + impls
│   │   │   ├── processor/                   # Business logic processors
│   │   │   └── service/                     # Service layer
│   │   │       ├── core/                    # Core services
│   │   │       │   ├── controller/          # Controller implementations
│   │   │       │   │   ├── input/           # Input sensor implementations
│   │   │       │   │   └── output/          # Output actuator implementations
│   │   │       │   └── module/              # Module implementations
│   │   │       └── util/                    # Utility classes
│   │   └── security/                        # Security layer
│   │       ├── auth/                        # Authentication
│   │       │   ├── ajax/                    # Ajax login flow
│   │       │   ├── jwt/                     # JWT token handling
│   │       │   └── controller/              # Auth REST endpoints
│   │       ├── config/                      # Security filters
│   │       ├── exceptions/                  # Security exceptions
│   │       ├── model/                       # Security models (User, Role, Token)
│   │       └── service/                     # Security services
│   └── resources/
│       ├── application.yml                  # Main config
│       ├── db-config.properties             # DB config properties
│       ├── logback-spring.xml               # Logging config
│       └── db/migration/                    # Flyway migrations (V1..V36)
└── test/
    └── java/technology/positivehome/ihome/
        └── server/
            ├── processor/                   # Processor tests
            └── service/core/controller/     # Controller tests (DR404)
```

## Coding Conventions

### Naming
- **Classes**: PascalCase (e.g., `PowerConsumptionStatBuilder`)
- **Methods/Variables**: camelCase (e.g., `getModuleSummary()`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `EntityType.MODULE`)
- **Packages**: lowercase, reverse domain (`technology.positivehome.ihome.*`)
- **Database tables**: lowercase with underscores (`module_config`, `controller_port_config`)
- **Flyway migrations**: `V<number>__<description>.sql`

### Architecture Patterns
- **Repository Pattern**: Interfaces + Impl classes for data access (no Spring Data JPA)
- **Row Mapper Pattern**: Custom `RowMapper` implementations for JDBC result mapping
- **Processor Pattern**: Business logic in `*Processor` classes (e.g., `SystemProcessor`, `StatisticProcessor`)
- **Module Pattern**: IoT automation modules extend `AbstractIHomeModule` or `AbstractRelayBasedIHomeModule`
- **Controller Pattern**: IoT hardware controllers implement `IHomeController` interface
- **Input/Output Pattern**: Sensors implement input interfaces, actuators implement output interfaces
- **Live/Emulated Pattern**: Each sensor/actuator has `Live*` and `Emulated*` implementations

### Key Conventions
- Use constructor injection (no field injection with `@Autowired`)
- Use `LogFactory.getLog()` from Apache Commons Logging for logging
- Custom `RowMapper` classes for JDBC queries (not JPA)
- Flyway for all database schema changes
- JWT tokens for API authentication
- Method-level security with custom permission evaluators

## Build System

### Gradle Tasks
- `./gradlew build` — Build the project
- `./gradlew test` — Run unit tests (filtered to `technology.positivehome.ihome.server.processor.*`)
- `./gradlew integrationTest` — Run integration tests
- `./gradlew bootRun` — Run the application locally (with debug port 40990)
- `./gradlew jib` — Build and push Docker image to registry
- `./gradlew jibDockerBuild` — Build Docker image to local daemon

### Configuration Properties
- `dockerRepository` — Docker registry (from `gradle.properties`)
- `testSpringConfLocation` — Path to test Spring config YAML (from `gradle.properties`)

## Testing

- JUnit 5 with JUnit Platform
- Tests are filtered to `technology.positivehome.ihome.server.processor.*` by default
- Use Mockito for mocking dependencies
- Spring REST Docs for API documentation (generated snippets in `build/generated-snippets/v1/`)
- Test config loaded via `-Dspring.config.additional-location`

## Docker / Containerization

- Uses **Google Jib** plugin (not Dockerfile) to build container images
- Base image: `eclipse-temurin:21-jdk-alpine`
- Image name: `$dockerRepository/ihome/app`
- Container runs as user `997:667`
- Exposes port `8080`
- JVM debug agent enabled on port `40990`

## Common Tasks

### Adding a New Module
1. Create a new class extending `AbstractRelayBasedIHomeModule` or `AbstractDimmerBasedIHomeModule`
2. Add Flyway migration for DB schema changes
3. Register in `ServicesConfiguration.java`
4. Add any new controller port config in the controller layer

### Adding a New Sensor
1. Create sensor data model in `domain/runtime/sensor/`
2. Create input implementation in `server/service/core/controller/input/` (Live + Emulated)
3. Add command model in `server/model/command/`
4. Register in `ServicesConfiguration.java`

### Adding a New REST Endpoint
1. Add controller method in appropriate `*Controller.java` in `server/controller/`
2. Add business logic in corresponding `*Processor.java` in `server/processor/`
3. Secure with method-level security annotations if needed
