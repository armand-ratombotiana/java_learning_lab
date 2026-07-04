# Why Clean Architecture Exists

## Historical Problems
- **Framework lock-in** - Business logic tied to frameworks
- **Database-centric design** - Architecture driven by database structure
- **UI coupling** - Business rules mixed with presentation logic
- **Testing difficulty** - Cannot test business rules without infrastructure
- **Architecture erosion** - Systems degrade into big balls of mud

## Business Drivers
- Long-lived systems that outlast frameworks
- Independent testability of business rules
- Technology independence and evolution
- Clear separation of concerns

## When Clean Architecture Makes Sense
- Complex business logic that is the primary value
- Long-lived enterprise applications
- Systems likely to change delivery mechanisms
- Applications requiring high test coverage
- Teams practicing DDD

## Relation to Other Architectures
- Clean Architecture unifies Hexagonal, Onion, and DDD layered approaches
- The dependency rule is the common principle across all
- Use cases correspond to application services in DDD
- Entities correspond to domain models
