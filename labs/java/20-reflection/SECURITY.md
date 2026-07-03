# Security — Reflection

## Bypassing Access Controls
```java
field.setAccessible(true); // Can read/write private fields — breaks encapsulation
```
This is a security risk. In Java 17+, modules restrict access by default.

## Module Encapsulation (Java 9+)
```java
// Throws InaccessibleObjectException in Java 17+
Field f = String.class.getDeclaredField("value");
f.setAccessible(true);
```
**Fix:** Use public APIs, or configure `--add-opens`.

## Reflective Code Injection
```java
Class<?> clazz = Class.forName(userInput); // Dangerous
Method m = clazz.getMethod(userMethod);    // Dangerous
```
**Fix:** Validate/whitelist class and method names from user input.

## Denial of Service
Repeated reflective calls on inaccessible objects can throw exceptions and degrade performance.

## Proxy Security
`InvocationHandler` can intercept and modify method return values — potential for tampering:
```java
(proxy, method, args) -> {
    if (method.getName().equals("getPassword")) {
        return "***hidden***"; // Could be exploited
    }
    return method.invoke(target, args);
}
```

## Best Practices
- Avoid reflection on untrusted classes
- Whitelist allowed classes/methods for user-controlled reflection
- Use `Proxy` only with trusted `InvocationHandler` implementations
- Know your module access rules (Java 9+)
