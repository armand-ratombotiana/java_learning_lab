# Debugging — Reflection

## Caching Reflection Objects
```java
// Avoid repeated calls
private static final Method CACHED_METHOD;
static {
    try {
        CACHED_METHOD = MyClass.class.getMethod("targetMethod");
    } catch (NoSuchMethodException e) {
        throw new ExceptionInInitializerError(e);
    }
}
```

## Checking Accessibility
```java
System.out.println(method.canAccess(instance)); // Java 9+
System.out.println(method.isAccessible()); // Legacy (deprecated)
```

## Common Exceptions
- `NoSuchMethodException` → wrong name or parameter types
- `IllegalArgumentException` → wrong argument types or counts
- `InvocationTargetException` → method threw an exception
- `NullPointerException` → target is null for instance method
- `InaccessibleObjectException` → Java 9+ module restriction

## Proxy Debug Dump
```java
// Generate proxy class to file
System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
// or Java 9+:
System.setProperty("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
```
Proxy classes will be written to `com/sun/proxy/$Proxy0.class`.

## Module Configuration
```bash
# List all open packages
java --list-modules

# Open package for reflection
--add-opens java.base/java.lang=ALL-UNNAMED
```
