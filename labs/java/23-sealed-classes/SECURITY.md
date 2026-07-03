# Security Considerations for Sealed Classes

## Preventing Unauthorized Subclassing

The primary security benefit of sealed classes is preventing unauthorized subtypes:

```java
// Security-critical framework
public sealed interface AuthenticationProvider 
    permits OAuthProvider, LDAPProvider, SAMLProvider {}

// Only known, vetted providers can be used
public final class OAuthProvider implements AuthenticationProvider { ... }
public final class LDAPProvider implements AuthenticationProvider { ... }
public final class SAMLProvider implements AuthenticationProvider { ... }

// An attacker cannot create EvilAuthenticationProvider
// because it's not in the permits clause
```

This provides a strong security guarantee:
- **Compile-time**: The compiler rejects unauthorized subclassing
- **Runtime**: The JVM's class loader also verifies the permits clause
- **Bytecode manipulation**: Even if an attacker modifies bytecode to add an extends clause, the JVM throws VerifyError at class loading

## Protecting Against Deserialization Attacks

When sealed classes are combined with records:

```java
public sealed interface Command 
    permits LoginCommand, LogoutCommand, TransferCommand {}

public record LoginCommand(String username, String password) implements Command {
    public LoginCommand {
        if (username == null || username.isBlank()) throw new IllegalArgumentException();
        if (password == null || password.length() < 8) throw new IllegalArgumentException();
    }
}

public record TransferCommand(String fromAccount, String toAccount, double amount) implements Command {
    public TransferCommand {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > 100000) throw new SecurityException("Amount exceeds limit");
    }
}
```

An attacker who crafts a serialized `TransferCommand` with a negative amount will have the validation fail during deserialization because records serialize through the canonical constructor.

## Type Confusion Prevention

Sealed classes prevent type confusion attacks where a subtype is used in a context expecting a different subtype:

```java
public sealed interface Permission permits ReadPermission, WritePermission, AdminPermission {}

// In a security check:
boolean checkAccess(Permission required, Permission granted) {
    return switch (required) {
        case ReadPermission __ -> switch (granted) {
            case ReadPermission __ -> true;
            case WritePermission __ -> true;  // Write implies read
            case AdminPermission __ -> true;  // Admin implies all
        };
        case WritePermission __ -> switch (granted) {
            case WritePermission __ -> true;
            case AdminPermission __ -> true;
            default -> false;
        };
        case AdminPermission __ -> switch (granted) {
            case AdminPermission __ -> true;
            default -> false;
        };
    };
}
```

Because the hierarchy is sealed, no unknown permission type can be injected to bypass these checks.

## Security in Framework Design

### Plugin Systems
```java
// Framework defines plugin extension points
public sealed interface Plugin permits UserPlugin {}
public non-sealed interface UserPlugin extends Plugin {
    void execute(ExecutionContext ctx);
}

// Users create their own plugins:
public class MyPlugin implements UserPlugin {
    public void execute(ExecutionContext ctx) { ... }
}
```

The `non-sealed` UserPlugin subtype acts as a controlled extension point. Users extend `UserPlugin`, not `Plugin` directly.

### Audit Trails
```java
public sealed interface AuditEvent 
    permits UserLoginEvent, DataAccessEvent, ConfigChangeEvent, SecurityAlertEvent {}

public record UserLoginEvent(String userId, boolean success, Instant timestamp) implements AuditEvent {}
public record DataAccessEvent(String userId, String resource, String action, Instant timestamp) implements AuditEvent {}
public record ConfigChangeEvent(String changedBy, String setting, String oldValue, String newValue) implements AuditEvent {}
public record SecurityAlertEvent(String userId, String alertType, String details, Instant timestamp) implements AuditEvent {}

// Audit processor handles ALL event types exhaustively
void processAuditEvent(AuditEvent event) {
    switch (event) {
        case UserLoginEvent(var uid, var success, var ts) -> logAccess(uid, "login", success, ts);
        case DataAccessEvent(var uid, var res, var act, var ts) -> logAccess(uid, res + ":" + act, true, ts);
        case ConfigChangeEvent(var by, var setting, var oldVal, var newVal) -> logConfigChange(by, setting, oldVal, newVal);
        case SecurityAlertEvent(var uid, var type, var details, var ts) -> triggerAlert(uid, type, details, ts);
    }
}
```

The sealed hierarchy ensures no audit event type can be created outside the defined set, preventing tampering with the audit trail.

## Limitations

### Non-Sealed Subtype Risk
A `non-sealed` subtype opens the hierarchy. Any class can extend a non-sealed subtype, potentially introducing security risks:

```java
// Security concern: non-sealed opens the door
public sealed interface Transaction permits SecureTransaction, OpenTransaction {}
public non-sealed interface OpenTransaction extends Transaction {}

// Anyone can create:
public class FraudulentTransaction implements OpenTransaction { ... }
```

**Mitigation**: Use `non-sealed` only when necessary. Audit all non-sealed subtypes in security-critical contexts.

### Class Loader Boundaries
If different class loaders load the same sealed class, they may have different permitted subclass lists. This is rarely an issue in standard application servers but should be considered in security-sensitive environments.

### Runtime Verification
The JVM's sealed class verification happens at class loading time. Once a class is loaded and verified, it remains loaded. Dynamic class reloading (in development tools like JRebel) must respect sealed constraints.

## Best Practices

1. **Prefer `final` subtypes**: Default to `final` for maximum security
2. **Use `sealed` for controlled extensibility**: Continue the sealed hierarchy only when needed
3. **Limit `non-sealed`**: Each `non-sealed` subtype is a potential security gap
4. **Combine with records**: Records provide additional security (immutability, canonical serialization)
5. **Validate in cooperative types**: Even sealed subtypes in the permits list should validate their inputs
6. **Monitor permits changes**: Adding new permitted subtypes is an API change that affects all consumers
