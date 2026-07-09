# Exceptions — Ultra Deep Dive

## 1. Exception Table Structure

Each method that catches exceptions has an **exception table** in its Code attribute:

```java
void example() {
    try {
        risky();
    } catch (IOException e) {
        handle();
    }
}
```

Exception table in bytecode:
```
Exception table:
 from    to  target type
    0     5     8   Class java/io/IOException

  Offset 0-5: try block range
  Offset 8:   catch handler (handle())
  Type:       Exception class
```

### Exception Table Entry Layout

Each entry has 4 fields:
| Field | Size | Description |
|-------|------|-------------|
| start_pc | u2 | Start of try block (bytecode offset) |
| end_pc | u2 | End of try block (exclusive) |
| handler_pc | u2 | Start of catch handler |
| catch_type | u2 | Constant pool index for exception class (0 = finally) |

### Catch-Type Matching

When an exception is thrown, the JVM walks the handler table:
1. Is the current PC between start_pc and end_pc?
2. Is the exception an instance of catch_type?
3. If both, jump to handler_pc

If catch_type is 0 (null), the handler catches ALL exceptions — this is how `finally` blocks work.

## 2. Stack Trace Construction Cost

Creating an exception is expensive — but the cost comes almost entirely from stack trace construction, not from object allocation.

### The `Throwable` Constructor

```java
public synchronized Throwable fillInStackTrace() {
    if (stackTrace != null || backtrace != null) {
        // Native call to JVM_FillInStackTrace
        fillInStackTrace0();
        // Stack trace is lazily converted to Java objects
    }
    return this;
}
```

### Cost Breakdown

```java
Exception e = new Exception();
```

What happens:
1. Object allocation (~16 bytes header + fields) — cheap
2. `fillInStackTrace0()` — walks the current stack, captures native frames
3. Stack frames are stored as native data (not Java objects) — this is the expensive part

**Relative costs**:
| Operation | Approximate time |
|-----------|-----------------|
| `new Object()` | ~10-20 ns |
| `new Exception()` | ~100-500 ns |
| `new Exception()` with deep stack (100 frames) | ~1000-5000 ns |
| `e.printStackTrace()` | ~100,000-1,000,000 ns (console I/O) |

### Lazy Stack Trace

The stack trace is NOT converted to Java objects until `getStackTrace()` is called:

```java
Exception e = new Exception();
// Stack frames captured in native format
StackTraceElement[] frames = e.getStackTrace();
// Now frames are converted to Java objects (lazily)
```

This means if you never call `getStackTrace()` or `printStackTrace()`, you avoid the conversion cost.

### Exception Cost Benchmark

```
Benchmark                            Mode  Cnt    Score    Error  Units
ExceptionBench.noException          avgt   10    3.45 ±  0.05  ns/op
ExceptionBench.createThrow          avgt   10  234.5  ±  2.1   ns/op
ExceptionBench.createCatch          avgt   10  456.7  ±  5.2   ns/op
ExceptionBench.createPrintStack     avgt   10  12345  ± 123    ns/op
```

## 3. try/catch/finally Bytecode

### Try-Catch-Finally Desugaring

```java
void example() {
    try {
        doSomething();
    } catch (IOException e) {
        handleIo();
    } finally {
        cleanup();
    }
}
```

The compiler generates:

```java
void example() {
    try {
        doSomething();
    } catch (IOException e) {
        handleIo();
    } catch (Throwable t) {  // Synthetic: catches any exception thrown in try block
        cleanup();
        throw t;
    }
    // Also: catches any exception thrown in catch handler
    try { } catch (Throwable t) { cleanup(); throw t; }
    cleanup();
}
```

### Complete Bytecode Exception Table

```
Exception table:
 from    to  target type
    0     5     8   Class java/io/IOException   // try → IOException handler
    0     5    20   any                          // try → synthetic finally handler
    8    12    20   any                          // catch → synthetic finally handler
   20    24    20   any                          // finally → itself (nested finally)
```

The `any` type entries are the `finally` blocks — they protect both the try and catch blocks.

### Finally Duplication

The `finally` block code is duplicated in the bytecode:
1. One copy in the successful path (after try/catch)
2. One copy in each exception handler path

This duplication means `finally` blocks are often compiled to multiple copies in the bytecode, increasing code size.

## 4. Suppressed Exceptions (Java 7+)

Try-with-resources introduced suppressed exceptions:

```java
try (AutoCloseable resource = new Resource()) {
    throw new IOException("Primary");
}  // Resource.close() throws another IOException
```

The `close()` exception is **suppressed** — added to the primary exception via `Throwable.addSuppressed()`:

```java
public final synchronized void addSuppressed(Throwable exception) {
    if (exception == this) throw new IllegalArgumentException();
    if (suppressedExceptions == null) {
        suppressedExceptions = new ArrayList<>();
    }
    suppressedExceptions.add(exception);
}
```

### Retrieving Suppressed Exceptions

```java
try { ... }
catch (IOException e) {
    Throwable[] suppressed = e.getSuppressed();
    for (Throwable s : suppressed) {
        System.err.println("Suppressed: " + s);
    }
}
```

### The Suppressed List in Serialization

Suppressed exceptions are serializable and are preserved through deserialization. The `Throwable` class serializes the `suppressedExceptions` list as an `ArrayList<Throwable>` in the serialization stream.

## 5. Checked vs Unchecked at Bytecode Level

At the bytecode level, there is NO distinction between checked and unchecked exceptions:

```java
// Source:
void method() throws IOException { }  // Checked exception

// Bytecode: no special attribute for checked exceptions
// The 'throws' clause is stored in the method's exceptions attribute:
// Exceptions:
//   throws java.io.IOException
```

The checked/unchecked distinction is **compile-time only**. The JVM does not enforce checked exceptions at runtime. You can call a method that declares `throws IOException` without catching or declaring it — the verifier doesn't care.

### Exception Checking in the Compiler

The Attr phase enforces checked exceptions:
```java
void caller() {
    method();  // Error: unhandled IOException
}
```

The compiler checks:
1. Is the exception a subclass of `RuntimeException` or `Error`? → unchecked, OK
2. Is it caught by an enclosing try-catch? → OK
3. Does the enclosing method declare `throws IOException`? → OK
4. Otherwise → COMPILE ERROR

## 6. The `Throwable` Hierarchy

```
Object
 └── Throwable (serializable)
      ├── Exception (checked, except RuntimeException)
      │    ├── RuntimeException (unchecked, superclass of runtime exceptions)
      │    │    ├── NullPointerException
      │    │    ├── IndexOutOfBoundsException
      │    │    ├── IllegalArgumentException
      │    │    ├── ClassCastException
      │    │    └── ...
      │    ├── IOException
      │    ├── SQLException
      │    ├── InterruptedException
      │    └── ...
      └── Error (unchecked, represents JVM failures)
           ├── OutOfMemoryError
           ├── StackOverflowError
           ├── VirtualMachineError
           └── ...
```

### When to Use Each

- **Exception (checked)**: Recoverable conditions that the caller should handle
- **RuntimeException (unchecked)**: Programming errors (null checks, bounds checks)
- **Error (unchecked)**: JVM failures — almost never caught

## 7. Stack Trace Truncation

The JVM may truncate stack traces for performance:

```java
// By default, no truncation:
-XX:-OmitStackTraceInFastThrow  // Include stack traces always

// With optimization (default for JIT):
-XX:+OmitStackTraceInFastThrow  // Hot paths → no stack trace
```

When `OmitStackTraceInFastThrow` is enabled (default), the JVM may throw exceptions with empty stack traces if the same exception is thrown repeatedly in a hot path:

```java
for (int i = 0; i < 100000; i++) {
    try { risky(); }
    catch (NullPointerException e) {
        // After ~1000 iterations, stack trace may be omitted
        // to avoid performance penalty
    }
}
```

## 8. The `assert` Statement and Exceptions

```java
assert x != null : "x should not be null";
```

If assertions are enabled, this compiles to:
```java
if (!$assertionsEnabled) {
    throw new AssertionError("x should not be null");
}
```

The `$assertionsEnabled` is a synthetic static boolean field that is `true` only when assertions are enabled (`-ea`).

## 9. Multi-Catch (Java 7+)

```java
try { ... }
catch (IOException | SQLException e) {
    // e is effectively final
    logger.log(e);
}
```

### Bytecode of Multi-Catch

Multi-catch does NOT create a new exception hierarchy. The compiler duplicates the catch handler for each exception type:

```
Exception table:
 from    to  target type
    0     5     8   Class java/io/IOException
    0     5     8   Class java/sql/SQLException
```

The same handler offset (8) is listed for both types — the same bytecode runs for both.

## 10. Rethrowing Exceptions with Improved Type Inference (Java 7+)

```java
void method() throws IOException, SQLException {
    try {
        risky();
    } catch (Exception e) {  // Broad catch
        throw e;  // Java 7+: compiler infers e is IOException or SQLException
    }
}
```

Before Java 7, you'd need `catch (IOException | SQLException e)`. After Java 7, the compiler analyzes the try block to determine which checked exceptions `risky()` actually throws and allows rethrowing the broader type.
