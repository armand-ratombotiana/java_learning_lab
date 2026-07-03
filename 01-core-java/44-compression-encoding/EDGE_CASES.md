# Edge Cases & Pitfalls: Compression & Encoding

Handling binary data, text encodings, and compression streams is a minefield of corrupted data, memory leaks, and security vulnerabilities.

## 1. The Default Charset Corruption
*   **The Scenario**: You write data to a file: `new FileWriter("data.txt").write("Résumé");`
*   **The Pitfall**: `FileWriter` (prior to Java 11) uses `Charset.defaultCharset()`. If you develop on a Mac (UTF-8) but deploy to a Windows server (Windows-1252), the byte representation of 'é' will change. When another system tries to read the file assuming UTF-8, it will see the infamous replacement character () or random garbage.
*   **Mitigation**: Never rely on the OS default. Always explicitly specify `StandardCharsets.UTF_8` when converting between Strings and bytes, or when opening readers/writers. (e.g., `new OutputStreamWriter(fos, StandardCharsets.UTF_8)`).

## 2. The Zip Slip Vulnerability (Directory Traversal)
*   **The Scenario**: You write a utility to unzip user-uploaded `.zip` files. You read the `ZipEntry.getName()` and append it to a base directory to save the file.
*   **The Pitfall**: The ZIP format does not enforce security. An attacker can craft a ZIP file where a `ZipEntry` has the name `../../../../etc/passwd` or `..\..\..\Windows\System32\malware.exe`. If you blindly append this to your base directory, the OS will resolve the `../` paths, allowing the attacker to overwrite critical system files outside of your intended extraction folder.
*   **Mitigation**: Always validate the canonical path of the extraction target.
    ```java
    File destFile = new File(targetDir, zipEntry.getName());
    String destDirPath = targetDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();
    
    if (!destFilePath.startsWith(destDirPath + File.separator)) {
        throw new SecurityException("Zip Slip vulnerability detected!");
    }
    ```

## 3. Base64 Padding and URL Safety
*   **The Scenario**: You Base64 encode a string and append it as a query parameter in a URL: `http://api.com?token=YWJj/def+`.
*   **The Pitfall**: Standard Base64 uses the characters `+` and `/`. In a URL, `+` is often interpreted as a space character. When the server decodes the token, the `+` is gone, the Base64 string is invalid, and decoding fails. Furthermore, Base64 adds `=` characters to the end of the string for padding, which can also cause parsing issues in certain strict URL parsers.
*   **Mitigation**: Use `Base64.getUrlEncoder()`. This replaces `+` with `-` and `/` with `_`. If you want to drop the padding entirely (common in JWTs), use `Base64.getUrlEncoder().withoutPadding()`.

## 4. GZIP Stream Truncation (Missing `close()`)
*   **The Scenario**: You wrap a `FileOutputStream` in a `GZIPOutputStream`. You write data to it, and then your method returns. You rely on the Garbage Collector to eventually clean up the stream.
*   **The Pitfall**: The GZIP format requires a specific "trailer" (a checksum and size marker) to be written at the very end of the file. This trailer is only written when you call `close()` or `finish()` on the `GZIPOutputStream`. If you don't close the stream, the resulting `.gz` file will be corrupt and cannot be decompressed by tools like `gunzip` or Java's `GZIPInputStream`.
*   **Mitigation**: Always use a `try-with-resources` block for compression streams. It guarantees that `close()` is called, writing the trailer and flushing the buffer.

## 5. In-Memory Compression Exhaustion
*   **The Scenario**: You receive a byte array over the network. You want to decompress it. You wrap a `ByteArrayInputStream` in a `GZIPInputStream` and read it fully into a `ByteArrayOutputStream`.
*   **The Pitfall**: This is a classic "Zip Bomb" vulnerability. An attacker can send a tiny 10KB GZIP file that decompresses into 10 Gigabytes of zeros. If you read the entire stream into memory, your JVM will crash with an `OutOfMemoryError`.
*   **Mitigation**: Never decompress unknown data entirely into memory. Always decompress in chunks (e.g., a 4KB buffer) and write it to a disk file, or enforce a strict maximum decompressed size limit and throw an exception if the limit is exceeded.