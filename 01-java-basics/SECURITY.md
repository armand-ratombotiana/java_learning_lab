# Security in Java Basics

## Input Validation

```java
// VULNERABLE - SQL Injection
String query = "SELECT * FROM users WHERE id = " + userId;

// SECURE - Parameterized query
PreparedStatement stmt = conn.prepareStatement(
    "SELECT * FROM users WHERE id = ?");
stmt.setString(1, userId);
```

## Buffer Overflow Prevention

Java automatically prevents buffer overflows:
- Array bounds checking
- Type safety at compile/runtime
- No pointer arithmetic

## Sensitive Data

```java
// Use char[] for passwords (overwrite after use)
char[] password = readPassword();
try {
    authenticate(password);
} finally {
    // Clear sensitive data
    Arrays.fill(password, '0');
}

// Never log sensitive data
logger.debug("User login: " + username);  // OK
logger.debug("Password: " + password);    // VULNERABLE
```

## Integer Overflow

```java
// VULNERABLE - could overflow
int total = 0;
for (int i = 0; i < items.size(); i++) {
    total += items.get(i).getPrice();
}

// SAFER - use BigInteger or check bounds
BigInteger total = BigInteger.ZERO;
for (Item item : items) {
    total = total.add(BigInteger.valueOf(item.getPrice()));
}
```

## Path Traversal

```java
// VULNERABLE
File file = new File(directory + "/" + filename);

// SECURE
Path basePath = Paths.get(directory).toAbsolutePath().normalize();
Path filePath = basePath.resolve(filename).normalize();
if (!filePath.startsWith(basePath)) {
    throw new SecurityException("Path traversal detected");
}
```

## Best Practices

1. Validate all user input
2. Use parameterized queries
3. Never hardcode credentials
4. Clear sensitive data after use
5. Use secure random for security-critical values
6. Encode output for XSS prevention