# Architectural Implications of Java Syntax

## How Syntax Shapes Architecture

Java's syntax directly influences how systems are designed, organized, and maintained.

### Package Structure Maps to Directory Layout

The `package` statement ties source code structure to filesystem layout. This has architectural implications:

```java
package com.company.project.module;
```

Must reside in `com/company/project/module/`. This enforces:
- Logical grouping of related classes
- Consistent naming across large codebases
- Clear ownership boundaries

### Access Modifiers Enforce Encapsulation

```java
public class Service {
    private Repository repo;          // Hidden implementation detail
    public void execute() { ... }     // Public API
    protected void onComplete() { ... } // Extension point for subclasses
}
```

This syntax-level encapsulation maps to architectural layers:
- `public` = API boundary
- `package-private` (default) = internal module collaboration
- `protected` = inheritance-based extension
- `private` = implementation detail

### Interface Syntax Enables Contracts

```java
public interface PaymentGateway {
    PaymentResult charge(CreditCard card, Money amount);
}
```

The `interface` keyword defines architectural contracts independent of implementation. This enables:
- Dependency injection
- Mocking for testing
- Multiple implementations (e.g., StripeGateway, PayPalGateway)
- Microservice communication contracts

### Final Classes Prevent Unintended Inheritance

```java
public final class ImmutableConfig {
    // Cannot be subclassed — prevents architecture erosion
}
```

Marking classes `final` communicates design intent: "this class is not designed for extension." This is an architectural decision that prevents fragile base class problems.

### Abstract Classes Define Template Architectures

```java
public abstract class DataExporter {
    public final void export() {
        openConnection();
        writeHeader();
        writeData();
        writeFooter();
        closeConnection();
    }
    protected abstract void writeHeader();
    protected abstract void writeData();
    protected abstract void writeFooter();
}
```

The Template Method pattern is encoded in syntax — `abstract` methods define what subclasses must provide, while `final` methods define invariant algorithm structure.

### Static Imports and Utility Classes

```java
import static com.google.common.base.Preconditions.checkNotNull;
```

Static imports can lead to flat, procedural-looking code. Architectural guidance: use sparingly. Overuse creates hidden dependencies and makes it unclear where functions come from.

### Annotations as Configuration

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) { ... }
}
```

Annotations are a syntax-level mechanism for declarative configuration. They shape architectural patterns:
- Reduces boilerplate XML configuration
- Enables convention-over-configuration frameworks
- Allows cross-cutting concerns (security, transactions) via declarative syntax

### Gradle/Maven and Syntax

Build tools interpret Java source syntax to determine module structure. The `module-info.java` file (Java 9+) uses specific syntax:

```java
module com.example.myapp {
    requires java.sql;
    exports com.example.myapp.api;
    provides com.example.myapp.spi.Service with com.example.myapp.impl.ServiceImpl;
}
```

This syntax enforces modular architecture by making dependencies and exports explicit.

### Architecture Decision Records and Syntax

When choosing syntax features, consider architectural impact:

| Syntax Decision | Architectural Impact |
|----------------|---------------------|
| Use `record` vs class | Immutability, value semantics |
| Use `sealed` interface | Exhaustive pattern matching, controlled hierarchy |
| Use `var` | Type inference reduces verbosity but may reduce readability |
| Use checked exceptions | Forces callers to handle errors or declare them |
| Use `final` | Prevents inheritance, enables JIT inlining |
| Use `synchronized` | Built-in locking, but affects scalability |
