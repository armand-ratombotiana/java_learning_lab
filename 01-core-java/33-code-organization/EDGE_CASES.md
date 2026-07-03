# Module 33: Code Organization - Edge Cases & Pitfalls

---

## Pitfall 1: Circular Dependencies

### ❌ Wrong
Package A depends on classes in Package B, and Package B depends on classes in Package A. This creates a tightly coupled "big ball of mud."
```java
// Package A
public class UserService {
    private BillingService billing; // Depends on Package B
}

// Package B
public class BillingService {
    private UserService user; // Depends on Package A
}
```

### ✅ Correct
Introduce an interface in a shared core package, or extract the shared logic into a completely new package (Package C) that both A and B can depend on without relying on each other.

---

## Pitfall 2: Over-Engineering Clean Architecture

### ❌ Wrong
Applying Hexagonal or Clean Architecture principles to a simple 3-table CRUD application. Creating 5 layers of mapping (DTO -> Command -> Domain -> Entity -> View) for a trivial data entry app.

### ✅ Correct
Choose your architecture based on domain complexity. For simple CRUD, a standard 3-tier architecture (Controller -> Service -> Repository) is perfectly fine. Reserve Ports and Adapters for complex domains.

---

## Pitfall 3: Making Everything Public

### ❌ Wrong
Declaring every class and method `public` by default, even if it's only meant to be an internal utility for a specific package.

### ✅ Correct
Use package-private (default visibility) classes to hide implementation details from other packages. If using Java 9+ JPMS, encapsulate internal packages so they aren't exported.