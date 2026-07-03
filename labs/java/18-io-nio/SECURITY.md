# Security — I/O & NIO

## Path Traversal
```java
// Vulnerable
Path path = Path.of("/app/data/" + userInput);
Files.readAllBytes(path); // user could input "../../etc/passwd"
```
**Fix:** Sanitise and validate the path. Use `resolve()` with `normalize()`:
```java
Path base = Path.of("/app/data");
Path resolved = base.resolve(userInput).normalize();
if (!resolved.startsWith(base)) throw new SecurityException();
```

## Symbolic Link Attacks
```java
if (!Files.isSymbolicLink(path)) {
    // Race condition: link may be swapped between check and use
}
```
**Fix:** Use `Files.readAllBytes()` directly with appropriate access controls.

## Temporary Files
```java
// Secure temp file creation
Path tmp = Files.createTempFile("app", ".tmp");
// Set restrictive permissions (POSIX)
Files.setPosixFilePermissions(tmp, Set.of(OWNER_READ, OWNER_WRITE));
```

## Resource Exhaustion
Always close streams/channels to prevent file descriptor leaks.

## Sensitive Data in Files
Use `Files.write()` with appropriate permissions. Overwrite sensitive files after use:
```java
Files.write(path, new byte[fileSize]); // Overwrite
Files.delete(path);
```
