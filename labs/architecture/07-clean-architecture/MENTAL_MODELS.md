# Mental Models for Clean Architecture

## The Onion Model
Like an onion, each layer wraps around the core. The core (entities) is the most stable, and outer layers are more volatile.

## The Castle Analogy
Entities are the castle keep - the most protected area. Use cases are the inner courtyard - where strategic decisions happen. Gateways are the castle walls with gates (interfaces). Frameworks are the surrounding landscape.

## The Human Body
Entities are like organs (heart, lungs) - essential business rules that rarely change. Use cases are like muscle movements - they orchestrate how organs work together. Controllers are like skin - the interface with the outside world.

## The Screaming Architecture
A clean architecture project structure tells you what the system does (it "screams" its purpose). If you see packages like "orders", "payments", "inventory" at the top level, you know it's an e-commerce system.

## The Dependency Inversion Triangle
```
High-level policy (Entities, Use Cases)
        ^
        | depends on interface
        |
Low-level detail (Controllers, Repositories)
```
The abstraction (interface) belongs to the high-level policy, not the low-level detail.
