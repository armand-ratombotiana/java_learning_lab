# Module 37: Clean Architecture - Edge Cases & Pitfalls

---

## Pitfall 1: Domain Entities Acting as ORM Entities

### ❌ Wrong
Using JPA/Hibernate annotations (`@Entity`, `@Table`) directly on your Clean Architecture Domain Entities. This violates the dependency rule because the inner circle (Domain) now depends on an outer circle detail (the Database Framework).

### ✅ Correct
Keep your Domain Entities pure POJOs. Create separate Database Entity classes in the Infrastructure/Adapter layer, and use a Mapper to convert between the DB Entities and Domain Entities.

---

## Pitfall 2: Bypassing the Use Case Layer

### ❌ Wrong
Having a REST Controller call a Database Repository directly for a seemingly simple query, skipping the Use Case layer because "it doesn't do anything anyway."

### ✅ Correct
All interactions from the outside world must pass through the Use Case layer. Even if the Use Case is a simple pass-through today, maintaining the boundary ensures that when business logic is inevitably added tomorrow, the architecture remains intact.

---

## Pitfall 3: Leaking Web/UI Concepts Inward

### ❌ Wrong
Passing `HttpServletRequest` or Spring's `ResponseEntity` objects into the Use Case layer.

### ✅ Correct
Controllers must unpack web-specific objects into plain Java objects (DTOs or Primitives) before calling the Use Case layer. The Use Case should have no idea it is being triggered by an HTTP request.