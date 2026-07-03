# Exceptions — Theoretical Foundation

## Exception Hierarchy

```
Throwable
├── Error (unchecked — serious JVM problems)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── VirtualMachineError
└── Exception
    ├── RuntimeException (unchecked — programming bugs)
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   ├── IllegalArgumentException
    │   └── ArithmeticException
    └── Checked exceptions (must be caught or declared)
        ├── IOException
        ├── SQLException
        ├── ClassNotFoundException
        └── InterruptedException
```

## Checked vs Unchecked

### Checked Exceptions
- Subclasses of `Exception` except `RuntimeException`
- Must be caught with `try-catch` or declared with `throws`
- Represent recoverable, expected failure conditions

```java
// Compiler forces handling:
public void readFile(String path) throws IOException {
    Files.readString(Path.of(path));  // IOException is checked
}
```

### Unchecked Exceptions
- Subclasses of `RuntimeException` and `Error`
- No handling required
- Represent programming errors or unrecoverable conditions

```java
// No handling required (but can be caught):
int x = 5 / 0;  // ArithmeticException (unchecked)
```

## try-catch-finally

```java
try {
    // Code that may throw an exception
    riskyOperation();
} catch (SpecificException e) {
    // Handle specific exception type
    log.error("Specific error", e);
} catch (GeneralException e) {
    // Catch broader exceptions after specific ones
    log.error("General error", e);
} finally {
    // Always executes (even if return or exception in try/catch)
    cleanup();
}
```

## Multi-Catch (Java 7+)

```java
try {
    process();
} catch (IOException | SQLException e) {
    // e is implicitly final
    log.error("IO or SQL error", e);
}
```

## Try-With-Resources (Java 7+)

Automatically closes resources implementing `AutoCloseable`:

```java
try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"));
     Scanner scanner = new Scanner(reader)) {
    // Resources are closed automatically in reverse order
    String line = reader.readLine();
}
```

Suppressed exceptions: if both try body and close() throw exceptions, the close exception is suppressed. Use `e.getSuppressed()` to inspect.

## Throwing Exceptions

```java
public void validateAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("Age cannot be negative: " + age);
    }
}
```

## Custom Exceptions

```java
public class InsufficientFundsException extends Exception {
    private final double balance;
    private final double amount;

    public InsufficientFundsException(double balance, double amount) {
        super("Insufficient funds: balance=" + balance + ", required=" + amount);
        this.balance = balance;
        this.amount = amount;
    }

    public double getBalance() { return balance; }
    public double getShortfall() { return amount - balance; }
}
```

## Exception Propagation

If an exception is not caught in the current method, it propagates up the call stack:

```
methodA() calls methodB() calls methodC()
methodC throws exception → methodB → methodA → main → JVM (prints stack trace and exits)
```

## Best Practices

1. Catch specific exceptions, not `Exception` or `Throwable`
2. Don't swallow exceptions (empty catch blocks)
3. Log exceptions with sufficient context
4. Use custom exceptions for domain-specific errors
5. Prefer try-with-resources for closeable resources
6. Throw early, catch late
7. Don't use exceptions for normal control flow
8. Preserve the original exception when rethrowing (use `cause` parameter)
