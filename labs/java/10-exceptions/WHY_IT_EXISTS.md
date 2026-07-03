# Why Exceptions Exist

## The Problem Exceptions Solve

Traditional error handling uses return codes: every function returns a status value that callers must check. This leads to deeply nested conditionals and makes it easy to forget checking an error code. Exceptions separate error handling from normal control flow, making both cleaner.

## Historical Context

C uses return codes and `errno` — a global error variable that can be overwritten. C++ introduced exceptions as a structured alternative. Java's exception system was designed from scratch:

- **Checked exceptions**: Force callers to handle or declare exceptions — a Java innovation
- **Unified hierarchy**: All exceptions extend `Throwable` — consistent `try-catch-finally` for all errors
- **Stack traces**: Every exception captures the call stack at creation time — invaluable for debugging
- **Try-with-resources**: Automatic resource cleanup (Java 7) — eliminates finally blocks for closing resources

The checked vs unchecked distinction was controversial. Proponents argue it forces robust error handling. Critics (including many in the Java community) argue it clutters APIs. Modern frameworks (Spring) tend to use unchecked exceptions with global exception handlers.
