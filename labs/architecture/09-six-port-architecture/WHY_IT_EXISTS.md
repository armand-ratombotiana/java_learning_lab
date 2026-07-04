# Why Six-Port Architecture Exists

## Historical Problems
- **Ambiguous ports** - Hexagonal doesn't distinguish port types explicitly
- **Missing event ports** - Traditional ports don't handle event-driven flows well
- **Ad hoc naming** - Teams invent inconsistent port naming conventions
- **Incomplete abstraction** - Some external interactions are left without ports

## Business Drivers
- Need for explicit categorization of integration points
- Microservices with multiple interaction types
- Event-driven systems needing clear port definitions
- Standardization across large engineering organizations

## When Six-Port Makes Sense
- Large teams needing architectural conventions
- Systems with complex external integration patterns
- Event-driven microservices
- Projects transitioning from layered to port/adapter architecture
- Environments requiring architecture documentation via code

## Comparison
| Aspect | Hexagonal | Six-Port |
|--------|-----------|----------|
| Port types | General | 6 explicit types |
| Events | Implicit | Explicit event ports |
| Naming convention | Flexible | Prescribed |
| Learning curve | Moderate | Moderate+ |
