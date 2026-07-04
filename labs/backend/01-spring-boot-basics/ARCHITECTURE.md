# Architecture

## Layered Architecture with Spring Boot

```
┌─────────────────────────────────────────┐
│            Presentation Layer           │
│    @RestController / @Controller        │
├─────────────────────────────────────────┤
│           Service Layer                 │
│    @Service (business logic)            │
├─────────────────────────────────────────┤
│           Persistence Layer             │
│    @Repository / JPA Repositories        │
├─────────────────────────────────────────┤
│           Cross-Cutting                 │
│    Security / Caching / Auditing        │
└─────────────────────────────────────────┘
```

## Microservice Architecture
```
┌──────────┐  ┌──────────┐  ┌──────────┐
│ Service A│  │ Service B│  │ Service C│
│  (Boot)  │  │  (Boot)  │  │  (Boot)  │
└────┬─────┘  └────┬─────┘  └────┬─────┘
     │              │              │
     └──────────────┼──────────────┘
                    │
           ┌────────┴────────┐
           │  Service Registry│
           │  (Eureka/Consul) │
           └─────────────────┘
```

## Multi-Module Maven Structure
```
parent-pom/
├── common/          # Shared DTOs, utilities
├── api/             # REST API layer
├── domain/          # Business logic
├── infrastructure/  # Data access, messaging
└── application/     # Spring Boot application
```
