# Exceptions — Evolution Across Java Versions

## Java 1.0 (1996)

Complete exception system: `try-catch-finally`, `throw`, `throws`, checked vs unchecked exceptions, `Throwable` hierarchy with `Exception` and `Error` branches. The `finally` block guaranteed cleanup code execution.

## Java 1.4 (2002)

- **Chained exceptions**: `Throwable.initCause()` and `Throwable(Throwable cause)` constructor. Earlier versions couldn't chain exceptions — the root cause was lost when wrapping.

## Java 5 (2004)

- **`Thread` changes**: `Thread.stop()` and `Thread.suspend()` deprecated — their inherent exception-throwing behavior was unsafe
- **Annotations**: `@SuppressWarnings("unchecked")` for generic-related exceptions
- **`Throwable` enhancements**: Stack trace accessibility, `getStackTrace()`, `setStackTrace()`

## Java 7 (2011) — Major Changes

- **Multi-catch**: `catch (IOException | SQLException e)` — handle multiple exception types identically
- **Try-with-resources**: `try (Resource r = new Resource()) { ... }` — automatic resource closure
- **Suppressed exceptions**: When multiple exceptions occur (try body + close), suppressed exceptions are preserved
- **Rethrow type inference**: Compiler infers the most specific exception type when rethrowing

```java
public <T extends Exception> void rethrow(Exception e) throws T {
    throw (T) e;  // Compiler now infers T more precisely
}
```

## Java 9 (2017)

- **Try-with-resources with effectively-final variables**: Resources can be declared outside try:
  ```java
  Resource r = new Resource();
  try (r) { ... }  // Java 9+: r must be effectively final
  ```

## Java 14 (2020)

- **Helpful NullPointerExceptions**: `-XX:+ShowCodeDetailsInExceptionMessages` (enabled by default in Java 15+). NPE messages now show exactly which variable was null:
  ```
  Cannot invoke "String.length()" because the return value of "getUser().getName()" is null
  ```

## Java 17 (2021) — LTS

- **Sealed classes**: Exception hierarchies can be sealed — controls which exception types exist
- **Pattern matching**: `catch` blocks can use pattern matching in future versions

## Java 21 (2023)

- **Unnamed patterns**: `catch (_ e)` — Java 21 preview allows unnamed exception variable when exception is not needed
- **Virtual threads**: Changed exception behavior — stack traces may be truncated for performance (~256 frames default)

## Exception Philosophy Evolution

Java's exception handling has focused on reducing boilerplate (multi-catch, try-with-resources) and improving diagnostics (helpful NPEs, chained exceptions). The trend continues toward making error handling both safer and more concise.
