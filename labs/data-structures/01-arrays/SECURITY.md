# Security Considerations for Arrays

## Buffer Overflow Prevention

Java arrays are bounds-checked at runtime, preventing classic buffer overflow attacks:

```java
int[] arr = new int[4];
arr[10] = 42;  // Throws ArrayIndexOutOfBoundsException — safe
```

This is a critical security advantage over C/C++ where buffer overflows lead to arbitrary code execution.

## Sensitive Data Cleansing

```java
// Sensitive data may linger in memory
byte[] password = getPassword().getBytes();
// ... use password ...
Arrays.fill(password, (byte) 0);  // Clear before GC
```

Arrays are not zeroed on GC — sensitive data may persist in memory dumps.

## Denial of Service via Huge Arrays

```java
// Attacker-controlled size
int size = Integer.MAX_VALUE;  // Requesting huge array
int[] arr = new int[size];     // OutOfMemoryError
```

Validate sizes before allocation. In ArrayList, repeated inserts can trigger excessive resizing — the 1.5x growth factor mitigates this vs 2x.

## Array Covariance Exploit

```java
void maliciousAdd(Object[] arr) {
    arr[0] = new Integer(42);  // ArrayStoreException if arr is String[]
}

String[] safe = new String[1];
maliciousAdd(safe);  // Throws at runtime — prevents type corruption
```

## Information Leakage via Uninitialized Arrays

Java zero-initializes all arrays, preventing information leaks from stale heap data:

```java
int[] arr = new int[10];
// All elements are 0 — cannot read previous heap contents
```

## TimSort Exploitation (2015 Bug)

`Arrays.sort()` for objects uses TimSort. A bug in Java 8's TimSort implementation could cause `ArrayIndexOutOfBoundsException` with crafted data (CVE-2015-4734). Fixed in Java 8u60.
