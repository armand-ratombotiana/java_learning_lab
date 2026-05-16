# Security with Design Patterns

## Security Concerns

### Singleton Security
- Global state can be modified by any code
- Difficult to mock for testing
- Can become attack vector if not properly controlled

**Solution**: Use dependency injection, limit mutation

### Factory Security
- Ensure factory validates input
- Protect against malicious input passed to factory

```java
class SecureFactory {
    public Object create(String type) {
        // Validate type to prevent injection
        if (!isValidType(type)) {
            throw new SecurityException("Invalid type");
        }
        // Then create
    }
}
```

## Security Patterns

### Protection Proxy
```java
interface SensitiveResource {
    Object get();
}

class SecureProxy implements SensitiveResource {
    private final SensitiveResource real;
    private final AuthService auth;
    
    public Object get() {
        if (!auth.isAuthorized()) {
            throw new SecurityException("Not authorized");
        }
        return real.get();
    }
}
```

### Facade Security
- Expose only what's needed
- Hide dangerous operations behind simple API

## Best Practices

1. **Minimize global state**: Use dependency injection
2. **Validate in factories**: Don't trust input
3. **Limit permissions**: Don't give code more access than needed
4. **Consider immutability**: Where possible