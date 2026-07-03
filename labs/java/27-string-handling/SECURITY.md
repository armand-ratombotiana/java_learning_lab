# Security: String Handling

## Sensitive Data in Strings

### Problem: Strings are Immutable
```java
String password = "secret123";
// Use password...

// Cannot clear the string! It stays in memory until GC
password = "dummy";  // Original "secret123" still in memory
```
The original string value remains in the String Pool and memory until garbage collected. Even after GC, the memory may not be zeroed.

### Solution: Use char[] for Passwords
```java
char[] passwordChars = readPassword();
// Use password...
Arrays.fill(passwordChars, '\0');  // Clear immediately
```

### Problem: String Interning
```java
String s = new String(sensitiveData);  // Not interned by default
s.intern();  // Now it's in the String Pool FOREVER
```
Never intern sensitive data. The String Pool is never garbage collected for interned strings.

## Memory Dump Risks
Strings in heap dumps are readable as plain text:
- Passwords, API keys, tokens in Strings are visible
- StringBuilder content remains until overwritten
- String Pool contains all interned strings

## Best Practices

### Never store secrets in Strings
```java
// BAD
String apiKey = "sk-1234...";
String password = "p@ssw0rd";

// BETTER
char[] apiKey = readFromSecureStore();
char[] password = readPassword();
// Zero after use
Arrays.fill(apiKey, '\0');
Arrays.fill(password, '\0');
```

### Avoid toString() on sensitive objects
```java
// Accidentally logs sensitive data
log.info("User: " + user);  // user.toString() may expose password field
```

## Security Properties of String Operations
- String comparison using == vs equals(): timing-safe? No — equals() short-circuits. Use `MessageDigest.isEqual()` for constant-time comparison of sensitive values
- String.hashCode() is not cryptographically secure (too fast, predictable)
- Regular expression matching can be vulnerable to ReDoS (ReDoS = Regex Denial of Service) — use Pattern.compile() with reasonable limits
