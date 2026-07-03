# Security: Enums

## Singleton Guarantee
Enums provide a guaranteed singleton for each constant:
- Serialization: Deserialization returns same instance via Enum.valueOf()
- Reflection: Cannot instantiate enum via Constructor.newInstance() (throws IllegalArgumentException)
- This makes enums the safest way to implement the Singleton pattern

## Enum vs Singleton Class
```java
// Singleton class — can break via reflection
class ConfigManager {
    private static final ConfigManager INSTANCE = new ConfigManager();
    // Can be broken with setAccessible(true) on constructor
}

// Enum singleton — cannot break
enum ConfigManager {
    INSTANCE;
    // Reflection throws: Cannot reflectively create enum objects
}
```

## Security Properties
- **Immutable by design**: Enum constants cannot be replaced at runtime
- **Type safety**: No invalid enum values (unlike int constants, cannot pass 123)
- **No injection**: Enum values are validated at compile time (unlike String constants)
- **Final class**: Enum class cannot be subclassed
- **Serialization safe**: Enum serialization preserves singleton regardless of custom readObject

## Risks
- **Enum constant exposure in heap dumps**: Values are readable
- **Enum.toString() may leak data**: Override to return safe values for sensitive enums
- **Enum constants in public APIs**: Exposing enum values reveals internal state to clients
- **Mutable enum fields**: Shared mutable state is not thread-safe

## Best Practices
- Use enums for finite, well-known sets (roles, permissions, states)
- Avoid exposing internal enums in public API
- Treat enum constants as immutable — don't add mutable fields
- For sensitive enums (e.g., user roles), use explicit authorization checks, not just enum membership
- Override toString() for user-facing enums to avoid leaking enum names
