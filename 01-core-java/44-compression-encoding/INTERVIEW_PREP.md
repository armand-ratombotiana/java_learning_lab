# Interview Preparation: Compression & Encoding

This document covers advanced questions related to character encodings, Base64, and compression vulnerabilities.

## Q1: Why should you never use `new String(byte[])` or `String.getBytes()` without specifying a `Charset`?
**Answer:**
If you omit the `Charset`, Java relies on `Charset.defaultCharset()`, which is determined by the Operating System's locale settings.
If a developer compiles a JAR on a Mac (which defaults to UTF-8) and writes a file, and then runs that same JAR on a Windows Server (which might default to Windows-1252), the bytes will be interpreted differently. Special characters (like accents, emojis, or non-Latin alphabets) will be permanently corrupted, often replaced by the `?` character.
You must **always** explicitly pass `StandardCharsets.UTF_8` to ensure deterministic behavior across all environments.

## Q2: Explain the purpose of Base64 encoding. Does it encrypt the data?
**Answer:**
Base64 does **not** encrypt data; it provides zero security.
Its purpose is to safely represent arbitrary binary data (like an image, a PDF, or a compiled Java class) using only printable ASCII characters (A-Z, a-z, 0-9, +, /).
Many text-based protocols (like JSON, XML, or HTTP Headers) cannot safely transmit raw binary bytes because certain byte values might be interpreted as control characters (like EOF or Line Feed), which would break the parser. Base64 encoding ensures the binary data survives transmission through these text-based systems. The trade-off is that it increases the payload size by 33%.

## Q3: What is the difference between standard Base64 and URL-Safe Base64?
**Answer:**
Standard Base64 uses the characters `+` and `/`.
If you place a standard Base64 string into a URL query parameter (e.g., `?token=abc+def/ghi`), web servers and browsers often interpret the `+` as a space character (URL encoding rules). When the server tries to decode the Base64 string, the `+` is missing, and the decoding fails.
URL-Safe Base64 (`Base64.getUrlEncoder()`) replaces the `+` with a `-` (hyphen) and the `/` with an `_` (underscore). These characters are safe to use in URLs and filenames without requiring additional URL-encoding.

## Q4: What is a "Zip Bomb" (or Decompression Bomb), and how do you protect your application from it?
**Answer:**
A Zip Bomb is a malicious archive file designed to crash the system reading it. An attacker creates a file consisting of gigabytes of repeating data (like zeros). Because the data is highly repetitive, it compresses down to a tiny size (e.g., 40 KB).
If your Java application receives this 40 KB file and attempts to decompress it fully into memory (e.g., into a `ByteArrayOutputStream`), it will expand to gigabytes, instantly causing an `OutOfMemoryError` and crashing the JVM.
**Protection**: Never decompress unknown data entirely into memory without limits. Decompress it in small chunks, and maintain a counter of the `totalBytesRead`. If `totalBytesRead` exceeds a safe threshold (e.g., 10 MB), throw a `SecurityException` and abort the process.

## Q5: What is the "Zip Slip" vulnerability?
**Answer:**
Zip Slip is a severe directory traversal vulnerability.
When extracting a ZIP file, the `ZipEntry` contains the name/path of the file to be extracted. An attacker can craft a ZIP file where the entry name is `../../../../etc/passwd`.
If the Java code blindly reads this name and appends it to the target extraction directory (e.g., `new File(targetDir, entry.getName())`), the OS will resolve the `../` paths, allowing the attacker to escape the target directory and overwrite critical system files.
**Protection**: Always validate the canonical path of the extracted file. Verify that `extractedFile.getCanonicalPath().startsWith(targetDir.getCanonicalPath())`.