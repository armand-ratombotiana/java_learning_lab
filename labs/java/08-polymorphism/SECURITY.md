# Security — Polymorphism

## Interface Contract Security

When programming to interfaces, ensure that all implementations adhere to security contracts. Don't assume implementation behavior.

## Dynamic Dispatch Bypass

A polymorphic call may execute unexpected code if a malicious subclass is substituted. Use `final` classes for security-critical components.

## instanceof Checks for Security

Use `instanceof` with caution — a malicious class can implement an interface and be substituted where a trusted implementation is expected.
```java
if (obj instanceof TrustedProcessor) {  // Any class implementing this!
    processor.process(sensitiveData);
}
```

## Strategy Pattern Security

The Strategy pattern selects implementations at runtime. Ensure the selection mechanism can't be manipulated to inject malicious strategies.

## Type Confusion

Java's type system prevents type confusion (unlike C/C++). Casts that fail throw `ClassCastException` rather than producing corrupted objects.
