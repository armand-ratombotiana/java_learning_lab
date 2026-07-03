# Why Reflection Matters

## Framework Flexibility
Reflection enables generic frameworks that work with any class — no need for boilerplate adapters.

## Runtime Discovery
```java
Class.forName("com.mysql.cj.jdbc.Driver"); // JDBC driver loaded by name
```

## Code Generation Alternative
Instead of generating code for every combination, reflection handles it dynamically.

## Testing & Diagnostics
- Unit test frameworks discover and invoke test methods
- Profilers inspect object graphs
- Mocking libraries create dynamic implementations

## The Price of Reflection
- Performance: ~10-100x slower than direct calls
- Security: can bypass access controls
- Type safety: errors become runtime exceptions
- Maintainability: harder to refactor
