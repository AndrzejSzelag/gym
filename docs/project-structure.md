# Project Structure

This document describes the **logical and physical structure** of the GYM application,
including package responsibilities, modular boundaries, and architectural conventions.

## High-Level Package Layout

```text
src/main/java/pl/szelag/gym/
├── client/     # Domain: Member lifecycle and address management
├── common/     # Cross-cutting: Auditing, Exceptions, I18n
├── config/     # Infrastructure: Security, MVC, Bean Wiring
├── user/       # Identity: RBAC, AuthN, AuthZ
├── utility/    # Framework-agnostic stateless helpers
└── GymApp.java # Bootstrap & Global Feature Activation
```

Each top-level package represents a distinct functional or cross-cutting concern.

## Feature-Oriented Modules

**client**

Manages the core business domain of gym memberships. It utilizes a **Vertical Slice** organization within the package
while maintaining strict internal layering.

- `entity`: JPA models inheriting from `AuditableEntity`.
- `factory`: Centralizes complex object creation and ensures domain invariants.
- `resolver`: Encapsulates custom logic for request-to-view resolution.

**Design note**:
- **Data Sanitization**: Entities are responsible for their own integrity. All input data (names, emails, phones) is automatically sanitized and normalized via `StringUtils` before persistence to ensure high data quality.
- **Thin Controllers**: Controllers act as orchestrators; all business-critical state transitions and domain logic reside in the `service` and `entity` layers.

```text
client
├── advice        # Controller advice for member-specific errors
├── controller    # MVC flows for member management
├── dto           # Data transfer objects (request / response)
├── entity        # JPA entities (inheriting AuditableEntity)
├── factory       # Domain object creation and composition
├── mapper        # Entity ↔ DTO mapping
├── repository    # Spring Data JPA repositories
├── resolver      # Request parameter and view resolution logic
├── service       # Business logic and transactional boundaries
└── view          # View models and presentation-specific objects
```

**user**

Handles the Security Identity domain. It acts as the bridge between raw persistence and Spring Security's `UserDetails`.

- `identity`: Specialized logic for principal resolution and `SecurityContext` interaction.
- `entity`: Audited `User` and `Role` models implementing RBAC.

```text
user
├── advice        # Security-related exception handling
├── controller    # Authentication and user management endpoints
├── dto           # Authentication and user-related DTOs
├── entity        # User and Role entities (audited)
├── factory       # User and role creation logic
├── identity      # Identity resolution for SecurityContext
├── repository    # User and role repositories
└── service       # Authentication and authorization logic
```

## Shared and Cross-Cutting Modules

**common**

The foundational layer of the application. It provides the "plumbing" that feature modules consume but never depends on them (**Zero Upward Dependency**).

```text
common
├── api             # Shared contracts and markers
├── exception       # Global hierarchy (e.g., BusinessException)
├── persistence     # Auditing infrastructure (AuditableEntity)
└── internationalization
```

**Design notes**:
- **AuditableEntity**: A `@MappedSuperclass` that centrally manages `created_at`, `created_by`, `updated_at`, and `last_modified_by` using Spring Data JPA Auditing.
- **Stability**: This module is designed to remain stable over time and be safe to reuse across different domains. It contains no direct dependencies on `client` or `user` modules.

**config**

The *8Infrastructure & Identity Integration Layer**. This module orchestrates the application's lifecycle, security filters, and the bridge between Spring Security and the domain model.

```text
config
├── ApplicationBootstrap.java    # System warming and initial data seeding
├── AuditConfig.java             # JPA Auditing & AuditorAware implementation
├── AuthUser.java                # Principal wrapper for Spring Security context
├── AuthUserDetailsService.java  # Bridge between User repository and Auth provider
├── SecurityConfig.java          # AuthN/AuthZ rules, CSRF, and Filter Chain
└── WebMvcConfig.java            # View resolvers, I18n, and resource mapping
```

Design notes:
- **Identity Abstraction**: `AuthUser` and `AuthUserDetailsService` are placed here to act as the "Security Glue". They prevent the core user domain from being directly coupled to Spring Security’s internal interfaces.
- **Centralized Governance**: All framework-specific annotations (e.g., `@EnableWebSecurity`, `@EnableJpaAuditing`) are localized here to keep the rest of the application as "Plain Old Java Objects" (POJO) as possible.
- **System Warming**: `ApplicationBootstrap` ensures the system reaches a consistent state upon startup, handling any necessary infrastructure checks or initial configurations.

**utility**

General-purpose helper classes.

```text
utility
```

**Design notes**:

- Stateless helpers only
- No Spring-managed beans
- Used sparingly and reviewed carefully to prevent accidental domain leakage or hidden coupling.

## Application Entry Point

**GymApp**

```text
GymApp.java
```

- Activates global features via `@EnableJpaAuditing`.
- Bootstraps the application context.

## Resources Structure

```text
src/main/resources
├── docker-init     # Database initialization scripts
├── lang            # Internationalization bundles
├── static          # Static assets (CSS, JS, images)
├── templates       # Thymeleaf templates
└── ValidationMessages.properties
```

**Design notes**:

- SQL initialization scripts are environment-aware
- I18n is centralized and explicit
- Presentation resources are fully separated from Java code

## Architectural Principles

The project structure enforces the following principles:

- **Automated Accountability**

Auditing is opt-out, not opt-in. By extending `AuditableEntity`, developers ensure that every record carries a cryptographically verifiable trail of "Who & When".

- **Layered Isolation**

The project follows a strict **unidirectional dependency flow**:

`Web (Controllers)` → `Logic (Services)` → `Data (Repositories)`.

- **Context-Aware Persistence**

Infrastructure concerns (Security Context) are propagated to the Database layer via `common.config`, preventing "security-leakage" into business service signatures.

- **Test-First Symmetry**

The source layout is mirrored in `src/test/java`. Integration tests (e.g., `EntityAuditIT`) verify the **Full-Stack Wiring** between `common`, `config`, and domain modules.

## Navigation Guidelines

1. **Entry Points**: Inspect `GymApp.java` for active profiles and global `@Enable...` toggles.
2. **Behavior**: Explore `service` packages for transactional boundaries and business rules.
3. **Security**: Audit `config/SecurityConfig.java` and `user/identity` to understand access control.
4. **Data**: View `common/persistence` for the base schema requirements.

## Dependency Rules (Architectural Contract)

| Source Layer              | Permitted Dependencies            | Prohibited Dependencies                            |
|:--------------------------|:----------------------------------|:---------------------------------------------------|
| **Feature (client/user)** | `common`, `utility`, `config`     | Other Feature Modules (use `common.api` if needed) |
| **common**                | Framework libs (Spring, JPA)      | **Any** Feature Module (`client`, `user`)          |
| **utility**               | Pure Java (JDK)                   | Spring Framework / Managed Beans                   |
| **service**               | `repository`, `factory`, `mapper` | `controller`, `servlet-api`                        |

Violations of these rules should be treated as architectural defects.

## Scope and Non-Goals

This document focuses on **structural intent**. It does not cover specific API routes, DTO field mappings, or deployment orchestrations.
Its purpose is to document **structural and architectural intent**, not implementation details.
