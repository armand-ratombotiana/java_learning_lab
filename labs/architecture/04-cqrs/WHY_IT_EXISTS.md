# Why CQRS Exists

## Historical Problems
- **CRUD mismatch** - Same model for reads and writes leads to compromises
- **Performance conflicts** - Read optimizations hurt write performance and vice versa
- **Complex queries** - ORM struggles with complex reporting queries
- **Scalability asymmetry** - Reads and writes have different scaling patterns
- **Security concerns** - Same model exposes data that shouldn't be mutable together

## Business Drivers
- Read-heavy workloads need different optimization
- Complex domains need separate write validation
- Reporting requirements differ from operational needs
- Audit trails require immutable write models

## When CQRS Makes Sense
- High read/write asymmetry (e.g., 90% reads, 10% writes)
- Complex domain logic on write side
- Multiple read representations needed
- Collaborative domains with concurrent edits
- Systems with different security requirements for reads vs writes
