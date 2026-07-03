# Security Considerations for Records

## Deserialization Security

### The Record Serialization Shield
Records provide automatic protection against deserialization attacks because deserialization always goes through the canonical constructor:

```java
public record SecureConfig(String databaseUrl, int maxConnections, boolean debugMode) {
    public SecureConfig {
        // Validation runs during deserialization too!
        if (databaseUrl == null || databaseUrl.isBlank()) {
            throw new IllegalArgumentException("Database URL required");
        }
        if (maxConnections < 1 || maxConnections > 100) {
            throw new IllegalArgumentException("Invalid max connections");
        }
        if (debugMode && "production".equals(System.getenv("ENV"))) {
            throw new SecurityException("Debug mode not allowed in production");
        }
    }
}
```

Traditional serialization bypasses constructors, so an attacker can craft a serialization stream that creates objects in invalid states. Records prevent this because the `ObjectInputStream` must call the canonical constructor.

### Protection Against Common Serialization Attacks
- **Builder gadget attack**: Records don't have setters or builder methods that could be chained maliciously
- **HashSet/Hashtable deserialization DoS**: Records' hashCode is deterministic (can't craft malicious hashCode values)
- **Fake serialVersionUID**: Records' serialized form includes actual component types, not just UID
- **readResolve/writeReplace bypass**: These methods are ignored for records

## Data Exposure via toString()

Record's toString() exposes all component values:

```java
public record User(String username, String password) {}
// toString() returns: User[username=alice, password=secret123]
```

**Risk**: Logging record instances may inadvertently expose sensitive data.
**Mitigation**: Override toString() in sensitive records:

```java
public record User(String username, String password) {
    @Override
    public String toString() {
        return "User[username=" + username + ", password=***]";
    }
}
```

## Data Exposure via equals()/hashCode()

`equals()` and `hashCode()` use all components. If a component changes after the record is used as a map key, the record will be "lost" in the map:

```java
// Security issue: record with mutable component used as map key
record Config(Map<String, String> settings) {}

var config = new Config(new HashMap<>());
var map = new HashMap<Config, String>();
map.put(config, "value");

config.settings().put("key", "value");  // Mutates the component!
// map.get(config) — may return null now (hashCode depends on mutable state)
```

**Fix**: Always use `List.copyOf()` or immutable collections in records to prevent this.

## Information Leakage via Accessor Methods

Record accessor methods expose component values. For security-sensitive components, restrict access:

```java
public record PaymentCard(String cardNumber, String expiryDate, String cvv) {
    public String maskCardNumber() {
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
    
    // Don't expose full card number via accessor
    public String cardNumber() {
        return maskCardNumber();  // Override the default accessor
    }
}
```

## Records and SecurityManager

Records work with `SecurityManager` (deprecated but still used). The canonical constructor's accessibility is checked during construction, just like regular classes.

## Thread Safety

Records are thread-safe by default (immutable). However, mutable components (like arrays or lists) introduce thread-safety concerns:

```java
public record SharedState(int[] values) {
    public SharedState {
        values = values.clone();  // Defensive copy at construction
    }
    
    public int[] values() {
        return values.clone();  // Defensive copy at access
    }
}
```

Without defensive copies, one thread could modify the array while another thread reads it.

## Records in Security-Critical Contexts

### Authentication Tokens
```java
public record AuthToken(String userId, String role, Instant expiresAt) {
    public AuthToken {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException();
        if (role == null || role.isBlank()) throw new IllegalArgumentException();
        if (expiresAt == null) throw new IllegalArgumentException();
    }
    
    public boolean isValid() {
        return Instant.now().isBefore(expiresAt);
    }
}
```

### Input Validation
```java
public record EmailAddress(String value) {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    public EmailAddress {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email: " + value);
        }
    }
}
```

## Best Practices

1. **Always validate in compact constructor**: Validation runs on all construction paths including serialization
2. **Defensively copy mutable components**: Use `List.copyOf()`, `clone()` for arrays
3. **Override toString() for sensitive data**: Mask passwords, tokens, PII
4. **Override accessors if needed**: Return masked versions of sensitive data
5. **Use immutable component types**: Prefer `List` over `ArrayList`, `Instant` over `Date`
6. **Avoid records for entities**: Entities have identity and state transitions — use classes
7. **Be aware of component exposure**: Every record component is public by design
8. **Test serialization security**: Verify that validation runs during deserialization
