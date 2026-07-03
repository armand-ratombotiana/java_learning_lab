# Security — Inheritance

## Fragile Base Class

Changes to superclass behavior can break subclass security guarantees. Mark security-critical classes as `final` to prevent subclassing.

## Method Override Hijacking

A malicious subclass can override methods to bypass validation:
```java
public class SecureValidator {
    public boolean validate(String input) {
        return checkInput(input);
    }
    protected boolean checkInput(String input) { ... }
}
```
Make validation methods `final` or `private` to prevent override.

## Final for Security

Security-sensitive classes should be `final`:
- `final class SecureConnection { ... }` — cannot be subclassed to intercept methods
- `final method validateCertificate()` — cannot be overridden to skip validation

## Constructor Security

If a subclass has access to superclass constructor parameters, ensure sensitive parameters aren't exposed.
