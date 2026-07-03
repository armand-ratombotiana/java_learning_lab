# Security — Arrays & Strings

## Buffer Overflow

Java arrays are bounds-checked — no buffer overflow risk. However, native code called via JNI can overflow.

## String Pool as Side Channel

String interning (`String.intern()`) stores strings forever — sensitive data in interned strings persists in memory.

## Passwords as Strings

Strings are immutable and stay in memory until GC. For passwords:
```java
char[] password = readPassword();
// use password
Arrays.fill(password, '\0');  // Clear immediately after use
```

## SQL Injection via String Concatenation

Never: `"SELECT * FROM users WHERE name = '" + name + "'"`
Use: `PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE name = ?"); ps.setString(1, name);`

## Command Injection

Never: `Runtime.exec("grep " + input)` — use `ProcessBuilder` with explicit arguments.

## Path Traversal

`new File(basePath + fileName)` — use `Path.normalize()` and validate against allowed directory.
