# Architecture: Module System

## Layered Architecture
```
Application Layer (com.example.app)
         │ requires
         ▼
Business Layer (com.example.business)
         │ requires
         ▼
Data Layer (com.example.data)
         │ requires
         ▼
Infrastructure Layer (java.sql, java.xml)
         │ requires
         ▼
Platform Layer (java.base, java.logging)
```

## Hexagonal Architecture with Modules
```
                   ┌──────────────┐
                   │  Domain      │
                   │  (exports    │
                   │   domain)    │
                   └──────┬───────┘
                          │ requires
              ┌───────────┴───────────┐
              │                       │
     ┌────────┴──────┐      ┌────────┴──────┐
     │  Adapter.In   │      │  Adapter.Out  │
     │  (uses domain)│      │  (uses domain)│
     └───────────────┘      └───────────────┘
```

## Microservices with Modules
Each microservice is a module:
- Internal packages are not exported (implementation hidden)
- API packages are exported to service consumers
- Service interfaces via provides/uses
- Shared modules for types (requires transitive)

## Module Architecture Patterns

### Facade Module
Exports a simplified API, hides complexity:
```java
module com.example.facade {
    exports com.example.facade.api;
    requires com.example.internal.complex;
}
```

### SPI Module
Defines service interfaces:
```java
module com.example.spi {
    exports com.example.spi;
}
```

### Plugin Module
Implements SPI, discovered via ServiceLoader:
```java
module com.example.plugin {
    requires com.example.spi;
    provides com.example.spi.Plugin
        with com.example.plugin.MyPlugin;
}
```

## Architecture Rules
1. Modules should be cohesive (single responsibility)
2. Avoid circular dependencies (enforced by module system)
3. Export only what is necessary (minimal API surface)
4. Use qualified exports for internal collaboration
5. Service interfaces belong in separate modules
6. Domain modules should not depend on infrastructure modules (Dependency Inversion)
