# Security: Modules

## Strong Encapsulation
The module system enforces strong encapsulation:
- Internal APIs (com.sun.*, jdk.internal.*) are no longer accessible by default
- Applications must explicitly open packages for reflection
- Module boundaries act as security barriers

## Reflection Control
Before Java 9, reflection could access any class field/method. With modules:
- Only opened packages allow reflective access
- setAccessible() respects module boundaries
- Module::addOpens and --add-opens control reflection at runtime
- Java 17+ enforces strong encapsulation by default (--illegal-access=deny)

## Security Benefits
- **API hardening**: Internal JDK APIs cannot be used (more stable code)
- **Reduced attack surface**: Fewer accessible classes means fewer vectors
- **Service isolation**: ServiceLoader only loads implementations from known modules
- **Resource control**: Module-specific resources prevent classloader attacks

## Attack Vectors Mitigated
- **Reflection attacks**: Cannot access internal JDK classes via reflection
- **Class loading attacks**: Cannot load classes from arbitrary packages
- **Dependency confusion**: Module path checks prevent loading wrong module versions
- **Privilege escalation**: Internal APIs (like sun.misc.Unsafe) are restricted

## Security Configuration
```bash
# Allow reflection for specific module (use with caution)
java --add-opens java.base/java.lang=ALL-UNNAMED

# List illegal access warnings
java --illegal-access=warn

# Deny all illegal access (Java 17+ default)
java --illegal-access=deny
```

## Best Practices
- Keep opens directives minimal (only for required frameworks)
- Use qualified opens when possible
- Avoid ALL-UNNAMED exports in production
- Regularly audit module-info.java permissions
- Use --illegal-access=deny to verify module encapsulation
