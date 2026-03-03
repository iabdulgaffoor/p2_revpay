# Application Architecture

RevPay follows a modern, monolithic **N-Tier Architecture** pattern.

```mermaid
graph TD
    subgraph "Frontend Layer"
        UI[Thymeleaf Templates]
        Statics[CSS / JS / Assets]
    end

    subgraph "Presentation Layer"
        WC[Web Controller - Session]
        RC[REST Controller - JWT]
    end

    subgraph "Service Layer"
        Interface[I-Service Interfaces]
        Impl[Service Implementations]
        Mapper[ModelMapper / DTOs]
    end

    subgraph "Data Layer"
        Repo[Spring Data JPA Repositories]
        DB[(Oracle 23ai Database)]
    end

    UI --> WC
    Statics --> UI
    Postman[Postman / Ext API] --> RC
    
    WC --> Interface
    RC --> Interface
    Interface --> Impl
    Impl --> Repo
    Repo --> DB
    Impl --> Mapper
```

## Layers Overview

### 1. Presentation Layer
- **Web Controllers:** Manage server-side rendering with Thymeleaf. Secured via HTTP Sessions.
- **REST Controllers:** Manage API interactions for mobile/third-party integration. Secured via JWT tokens.

### 2. Service Layer (Business Logic)
- This layer contains the core financial logic (e.g., calculating interest, validating wallet balances).
- It is decoupled from the UI/API layers via **Interfaces** (prefix: `I`).
- **DTOs (Data Transfer Objects):** Ensure that internal entity details (like password hashes) never reach the client.

### 3. Data Layer (Persistence)
- Uses **Spring Data JPA** with **Hibernate** for ORM mapping.
- Connected to an **Oracle 23ai** database for enterprise-grade data management.
- Transactional integrity is maintained using `@Transactional` at the service level.
