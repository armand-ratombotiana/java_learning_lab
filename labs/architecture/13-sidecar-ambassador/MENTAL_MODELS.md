# Mental Models: Sidecar Ambassador

## Model 1: The Architecture as a City
Think of the system as a city with different districts (services), roads (APIs), and infrastructure (utilities).

**Key insights:**
- Each district has its own purpose and zoning rules (bounded context)
- Roads connect districts with specific traffic rules (protocols)
- Utilities like water and power are shared infrastructure (cross-cutting concerns)
- City planning determines how districts evolve over time

## Model 2: The Tree Analogy
The system grows like a tree with roots, trunk, branches, and leaves.

**Key insights:**
- Roots are the foundation (infrastructure and platform)
- Trunk is the core logic (domain and business rules)
- Branches are different features and capabilities
- Leaves are the user interfaces and external integrations

## Model 3: The Orchestra Conductor
An orchestrated system is like an orchestra with a conductor.

**Key insights:**
- Each musician plays their part independently (service autonomy)
- The conductor coordinates timing and flow (orchestrator)
- Sheet music defines the expected behavior (contracts)
- Practice and performance are different modes (testing vs. production)

## Model 4: The Kitchen Brigade
A professional kitchen operates like a distributed system.

**Key insights:**
- Each station has specific responsibilities (service boundaries)
- Orders flow from station to station (workflow)
- The head chef coordinates the brigade (orchestrator)
- Mise en place is preparation for efficiency (caching and optimization)

## Model 5: The Shipping Container
Standardized containers revolutionized shipping. The same principle applies to service interfaces.

**Key insights:**
- Standard containers fit any ship, truck, or train (standardized interfaces)
- Content doesn't matter for transport (encapsulation)
- Stacking enables efficient scaling (composition)
- Specialized containers for special cargo (optimized services)

## Model 6: The Circuit Breaker (Electrical)
The software pattern directly mirrors electrical circuit breakers.

**Key insights:**
- Normal operation allows current to flow (CLOSED state)
- Fault detection trips the breaker (OPEN state)
- Automatic reset attempts after cooldown (HALF_OPEN state)
- Persistent faults require manual intervention (permanent failure)

## Applying These Models

### When Designing
- Use the city model to think about service boundaries
- Use the tree model to think about dependency hierarchy
- Use the orchestra model to think about coordination

### When Debugging
- Trace the flow like following a recipe in the kitchen
- Check each layer like inspecting tree health
- Verify connections like checking road maps

### When Scaling
- Think about adding more identical containers (horizontal)
- Think about making containers larger (vertical)
- Think about reorganizing the kitchen layout (restructuring)
