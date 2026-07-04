# Why Event Sourcing Exists

## Historical Problems
- **Lost history** - Traditional CRUD only stores current state, loses all context
- **Audit gaps** - No record of how state changed over time
- **Temporal queries** - Cannot query state at a point in time
- **Complex migrations** - Schema changes require complex data migrations

## Business Drivers
- Audit and compliance requirements
- Need for complete history and traceability
- Temporal query capabilities
- Event-driven system architectures
- CQRS integration requirements

## When Event Sourcing Makes Sense
- Systems requiring full audit trails
- Financial applications needing transaction history
- Systems that need temporal query capability
- Complex domains where event-driven design adds value
- Regulatory compliance environments

## Alternatives
- **Audit logging** - Separate audit table (current state + logs)
- **CDC (Change Data Capture)** - Database-level change tracking
- **Versioned tables** - Keeping history of changes in separate tables
