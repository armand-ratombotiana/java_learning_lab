# Exceptions — How It Works

## Step 1: try Block Setup

```java
try {
    riskyCode();
}
```

The compiler marks the bytecode range covered by the try block. It records the handler table in the class file:
```
Exception table:
  From: 0 (start of try)
  To:   10 (end of try)
  Target: 12 (catch handler)
  Type: IOException
```

## Step 2: Exception Throw

```java
throw new IOException("File not found");
```

1. `new` allocates IOException object
2. Constructor runs (captures stack trace)
3. `athrow` bytecode instruction executed
4. JVM searches for matching handler

## Step 3: Exception Propagation

The JVM unwinds the stack looking for a handler:
1. Check current method's exception table
2. If matching catch found: jump to handler, rest of try skipped
3. If no match: pop current frame, continue search in caller
4. If no handler in entire stack: thread terminates (print stack trace)

## Step 4: finally Block Execution

`finally` code is duplicated by the compiler:
1. Normal exit: inline after try or catch
2. Abnormal exit (exception): in catch handler, after handling, before rethrow
3. `jsr`/`ret` instructions (deprecated) or inline duplication (modern)

## Step 5: try-with-resources

```java
try (FileReader fr = new FileReader("file.txt")) {
    // use fr
}
```

Compiled equivalent:
```java
FileReader fr = new FileReader("file.txt");
Throwable primary = null;
try {
    // use fr
} catch (Throwable t) {
    primary = t;
    throw t;
} finally {
    if (fr != null) {
        if (primary != null) {
            try { fr.close(); } catch (Throwable t) { primary.addSuppressed(t); }
        } else {
            fr.close();
        }
    }
}
```

## Step 6: Checked Exception Verification

The compiler checks that checked exceptions are caught or declared:
1. For each method call in try block, note thrown checked exceptions
2. Verify each is caught by a catch clause or listed in this method's `throws`
3. If not caught and not declared → compilation error
