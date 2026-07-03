# Quizzes: Compression & Encoding

Test your knowledge of GZIP, ZIP, Base64, and Character Encoding.

## Quiz 1: Compression Streams

**Q1: What is the primary difference between GZIP and ZIP in Java?**
- A) GZIP is faster but compresses less.
- B) GZIP is used for compressing a single continuous stream of data, whereas ZIP is an archive format that contains a directory structure and multiple distinct files (`ZipEntry`).
- C) ZIP is only supported on Windows.
- D) GZIP cannot be used with network streams.
*Answer: B*

**Q2: You write data to a `GZIPOutputStream` but forget to call `close()`. What is the likely result?**
- A) The file will be uncompressed plain text.
- B) A memory leak occurs, but the file is fine.
- C) The resulting `.gz` file will be corrupt and unreadable because the required GZIP trailer (checksum and size) is only written when the stream is closed or finished.
- D) The JVM automatically closes it for you.
*Answer: C*

## Quiz 2: Encoding and Strings

**Q1: Why should you NEVER use `new String(byte[])` without specifying a `Charset`?**
- A) It is deprecated in Java 11.
- B) It is slower than specifying a Charset.
- C) It relies on the operating system's default character encoding. If the code runs on a machine with a different default encoding than the one used to generate the bytes, special characters will be permanently corrupted.
- D) It throws a `NullPointerException` if the array is empty.
*Answer: C*

**Q2: What is the purpose of Base64 encoding?**
- A) To compress large text files to save disk space.
- B) To encrypt sensitive passwords.
- C) To safely represent binary data (like an image or a compiled class) using only printable ASCII characters, so it can be transmitted over text-based protocols (like JSON or HTTP headers) without corruption.
- D) To convert UTF-8 characters to ASCII.
*Answer: C*

## Quiz 3: Security

**Q1: What is the "Zip Slip" vulnerability?**
- A) When a ZIP file decompresses to a size larger than the hard drive.
- B) When an attacker crafts a malicious ZIP file containing entries with relative paths like `../../etc/passwd`. If the extraction code blindly appends this to the target directory, it allows the attacker to overwrite critical files outside the intended extraction folder.
- C) When a ZIP file contains a virus.
- D) When the ZIP password is easily guessed.
*Answer: B*