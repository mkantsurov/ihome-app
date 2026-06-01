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
