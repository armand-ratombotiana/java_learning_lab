# Deep Dive: Compression & Encoding

## 1. The Need for Compression and Encoding
In network communication and file storage, two distinct transformations are frequently required:
*   **Compression**: Reducing the physical size (number of bytes) of data to save disk space or network bandwidth.
*   **Encoding**: Transforming binary data into a safe text format so it can be transmitted over text-based protocols (like HTTP headers or JSON) without corruption.

## 2. Compression in Java (`java.util.zip`)
Java provides built-in support for the DEFLATE compression algorithm, exposed primarily through GZIP and ZIP formats.

### GZIP (Single File/Stream)
GZIP is typically used to compress a single stream of data (like an HTTP response body). It does not contain an internal file directory structure.
*   **`GZIPOutputStream`**: Wraps an underlying `OutputStream`. Any data written to it is compressed on the fly and written to the underlying stream.
*   **`GZIPInputStream`**: Wraps an underlying `InputStream`. As data is read, it is decompressed on the fly.

```java
// Compressing a string to a file
try (FileOutputStream fos = new FileOutputStream("data.gz");
     GZIPOutputStream gos = new GZIPOutputStream(fos)) {
    gos.write("Hello World".getBytes(StandardCharsets.UTF_8));
}
```

### ZIP (Archive/Multiple Files)
ZIP is an archive format. It contains a directory structure and can hold multiple compressed files (entries).
*   **`ZipOutputStream`**: You must explicitly create a `ZipEntry` for each file before writing its data.
*   **`ZipInputStream`**: You must iterate through the entries using `getNextEntry()` before reading the data for that specific file.

```java
try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream("archive.zip"))) {
    ZipEntry entry = new ZipEntry("file1.txt");
    zos.putNextEntry(entry);
    zos.write("Data for file 1".getBytes());
    zos.closeEntry();
}
```

## 3. Base64 Encoding
Many protocols (like HTTP Basic Auth, JWT tokens, or embedding images in JSON) require binary data to be represented as printable ASCII text.
If you simply cast arbitrary bytes to a String (e.g., `new String(bytes)`), the OS will try to interpret random bytes as UTF-8 characters. It will fail, insert "replacement characters" (), and the data will be permanently corrupted.

**Base64** solves this by taking 3 bytes of binary data (24 bits) and splitting them into 4 chunks of 6 bits. Each 6-bit chunk maps to a safe, printable character (A-Z, a-z, 0-9, +, /).
*   *Trade-off*: Base64 encoding increases the size of the data by exactly 33%.

### Java 8+ `java.util.Base64`
Java 8 introduced a highly optimized, built-in Base64 API, rendering third-party libraries (like Apache Commons Codec) unnecessary for this task.

```java
String original = "user:password";
// Encode
String encoded = Base64.getEncoder().encodeToString(original.getBytes(StandardCharsets.UTF_8));
// Decode
byte[] decodedBytes = Base64.getDecoder().decode(encoded);
```

### URL-Safe Base64
Standard Base64 uses `+` and `/`. If you put this in a URL query parameter, the web server might interpret `+` as a space, corrupting the token.
Java provides `Base64.getUrlEncoder()`, which replaces `+` and `/` with the URL-safe characters `-` and `_`.

## 4. Character Encoding (`java.nio.charset`)
Character encoding is the process of mapping human-readable characters (like 'A' or 'é') to raw bytes.
*   **ASCII**: 1 byte per character. Only supports English letters and basic symbols.
*   **UTF-8**: Variable length (1 to 4 bytes per character). Backward compatible with ASCII. The absolute standard for the modern web.
*   **UTF-16**: 2 or 4 bytes per character. Used internally by Java for the `String` class.

**The Golden Rule**: Whenever you convert a `String` to a `byte[]` (or vice versa), you **MUST** specify the charset. If you don't, Java uses the operating system's default charset. If a developer compiles on a Mac (UTF-8) and runs on an old Windows Server (Windows-1252), the application will corrupt special characters.

```java
// BAD: Relies on OS default
byte[] bytes = "Café".getBytes(); 

// GOOD: Explicit, deterministic encoding
byte[] bytes = "Café".getBytes(StandardCharsets.UTF_8); 
```