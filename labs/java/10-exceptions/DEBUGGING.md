# Debugging Exceptions

## Reading Stack Traces

```
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.length()" because "s" is null
    at com.example.MyClass.process(MyClass.java:25)   ← FIRST FRAME = ORIGIN
    at com.example.MyClass.main(MyClass.java:10)      ← CALLER
```

Read from top to bottom. The topmost frame in your code (not library code) is likely where the bug is.

## Helpful NullPointerExceptions (Java 14+)

Enable with `-XX:+ShowCodeDetailsInExceptionMessages` (default in Java 15+):
```java
a.b.c.d();  // NPE says: "Cannot read field 'b' because 'a' is null"
```

## Resources Not Closed

Signs of resource leaks: "Too many open files", connection pool exhaustion:
1. Check that all `InputStream`, `Connection`, `ResultSet` are closed
2. Use try-with-resources — guaranteed close
3. Check for early returns before close in finally

## Swallowed Exceptions

When code silently fails:
1. Search for empty catch blocks
2. Search for `catch (Exception e) { }` or `catch (Throwable t) {}`
3. Search for `printStackTrace()` — usually insufficient in production
4. Check if exception is logged at wrong level (e.g., INFO instead of ERROR)

## Exception Performance

When exception handling is slow:
1. Creating an exception captures the stack trace (expensive)
2. Don't use exceptions for control flow
3. Lazy stack traces: override `fillInStackTrace()` for known paths
4. Use `-XX:-OmitStackTraceInFastThrow` to prevent JIT from eliding repeated NPEs

## Debugger Exception Breakpoints

- IntelliJ: Breakpoints → Exception Breakpoints → Add "NullPointerException"
- Stop on any exception, even if caught
- Configure: stop only in your code, not in library code
- Use "Exception Breakpoint" to catch the moment an exception is created
