# Security — Abstraction & Interfaces

## Interface Security Contracts

Interfaces define contracts. Document security requirements that implementations must follow. A `SecureConnection` interface should specify encryption requirements.

## Sealed Interfaces for Controlled Implementation

Sealed interfaces (Java 17+) restrict which classes can implement them — prevents unauthorized implementations.
```java
sealed interface PaymentProcessor permits TrustedProcessor, VerifiedProcessor {
    void processPayment(double amount);
}
```

## Default Method Security

Default methods can introduce security vulnerabilities if implementations don't override them. Document when implementations should override.

## Third-Party Implementations

When accepting third-party implementations of your interfaces, validate and sandbox appropriately. Use SecurityManager or module system.

## Functional Interface Hijacking

Lambdas can be used to inject arbitrary behavior via functional interfaces. Validate or restrict lambda sources for security-critical code.
