# Architecture Patterns - MENTAL MODELS

## Mental Model 1: The City Metaphor
Think of architecture patterns like city planning:
- **Layered**: A building with floors — each floor serves a purpose
- **Microservices**: A city of independent buildings, each with a function
- **Event-Driven**: A postal service — send messages without knowing recipients
- **CQRS**: Separate entrances for deliveries (writes) and visitors (reads)

## Mental Model 2: The Org Chart
Conway's Law: Systems mirror communication structures.
- Small startup (2-5 people): Layered works
- Multiple teams (5-20): Microservices
- Highly decoupled teams: Event-Driven

## Mental Model 3: The Kitchen Analogy
- **Layered**: Restaurant kitchen — prep (data), cooking (business), plating (presentation)
- **Microservices**: Food trucks — each truck does one thing well
- **Event-Driven**: Food delivery platform — orders broadcast to available cooks
- **CQRS**: Self-checkout kiosk (reads) vs kitchen tickets (writes)

## Mental Model 4: The CAP-Dependent View
- **AP systems** (Availability + Partition Tolerance): Event-Driven shines
- **CP systems** (Consistency + Partition Tolerance): Layered/Microservices with strong consistency
- **CA systems** (Consistency + Availability): Rare in distributed systems

## Decision Flow
```
Is your system simple (<5 services)? → Layered
  ↓ No
Do you need independent deployment? → Microservices
  ↓ No
Is async communication acceptable? → Event-Driven
  ↓ No
Are reads/writes asymmetric? → CQRS
  ↓ Otherwise
Consider combining patterns
```
