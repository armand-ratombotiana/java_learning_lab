# Exceptions — Internal Mechanics

## Exception Table

Each method's bytecode can have an exception table:
```
Exception table:
   start  end  handler  catch_type
       0   12       20  <Class java.io.IOException>
       0   12       40  <Class java.lang.Exception>
      20   38       40  <Class java.lang.Exception>
```

The JVM searches the table in order. The first matching handler (catch_type is superclass of thrown exception) is used.

## Stack Trace Capture

When an exception is created, `Throwable()` calls `fillInStackTrace()`:
1. JVM captures current stack frames
2. Each frame: class name, method name, source file, line number
3. Stack trace stored in Throwable (can be expensive — consider lazy creation for expected exceptions)

## Suppressed Exceptions

Introduced in Java 7 for try-with-resources. Stored as a list in Throwable. Access via `getSuppressed()`. Printed in stack trace as "Suppressed: ..."

## Chained Exceptions

```java
throw new MyException("cause", originalException);
// or:
MyException e = new MyException("cause");
e.initCause(originalException);
```

Causal chain stored as `cause` field. All constructors except in Throwable are either `(String, Throwable)`, `(Throwable)`, or call `initCause()`.

## Helpful NullPointerExceptions (Java 14+)

```java
a.b.c.d();
// NPE message: "Cannot read field 'b' because 'a' is null"
// or: "Cannot read field 'd' because 'a.b.c' is null"
```

The JVM uses bytecode analysis to determine exactly which variable was null. Disabled in performance-critical code (can be expensive to compute).

## Performance Considerations

- Creating an exception is expensive: stack trace capture requires walking the stack
- Throwing is even more expensive: stack unwinding, handler search
- Use exceptions for exceptional conditions, not control flow
- Reusing exceptions (with `fillInStackTrace` override) can reduce cost for known paths

## Java Flow Control vs Exception Cost

| Operation | Relative Cost |
|-----------|--------------|
| Normal return | 1x |
| if-else | 1x |
| try with no throw | 1-2x (JIT inlines) |
| throw and catch | 100-1000x |
| Stack trace capture | 10-100x (of throw) |
