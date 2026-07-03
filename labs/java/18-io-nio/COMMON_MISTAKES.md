# Common Mistakes — I/O & NIO

## 1. Not Closing Resources
```java
InputStream is = new FileInputStream("f");
is.read(); // Leak: never closed
```
**Fix:** Use try-with-resources.

## 2. Forgetting Encoding
```java
new FileReader("f.txt"); // Uses platform default encoding!
```
**Fix:** Always specify `StandardCharsets.UTF_8`.

## 3. Reading Bytes Into String Incorrectly
```java
String s = new String(is.readAllBytes()); // Wrong encoding assumption
```

## 4. Calling `read()` Without Looping
```java
is.read(buf); // May read fewer bytes than buf.length!
```
**Fix:** Use `readAllBytes()` or loop.

## 5. Forgetting `flip()` on ByteBuffer
Writing to buffer then reading without flipping returns garbage.

## 6. Using Default Buffer Size
Single-byte `read()` on unbuffered stream = syscall per byte → terrible performance.

## 7. Ignoring return value of `Files.delete()`
Use `Files.deleteIfExists()` when the file may not be present.

## 8. Path Traversal Vulnerabilities
Concatenating user input to paths without sanitisation.
