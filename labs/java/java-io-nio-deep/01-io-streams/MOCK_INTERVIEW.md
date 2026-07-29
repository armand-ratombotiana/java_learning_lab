# Mock Interview Transcript: I/O Streams

## Interviewer: Senior SWE, Google
## Candidate: Mid-level Java developer
## Time: 25 minutes
## Focus: I/O streams, performance, design patterns

---

**Q1: Walk me through Java's I/O stream architecture.**

**Candidate**: Java I/O uses the decorator pattern. The base classes are `InputStream`/`OutputStream` for bytes and `Reader`/`Writer` for characters. Concrete implementations like `FileInputStream` handle the source/sink. Decorators like `BufferedInputStream` wrap them to add functionality. The bridge classes `InputStreamReader`/`OutputStreamWriter` convert bytes to characters.

**Interviewer**: Why is buffering important for I/O performance?

**Candidate**: Without buffering, every `read()` call triggers a system call, which requires a context switch into kernel mode. A `BufferedInputStream` with an 8KB buffer reads 8192 bytes in one system call, reducing calls by 8192x. This is why `BufferedInputStream` can be orders of magnitude faster for small reads.

**Interviewer**: When would you choose `BufferedReader.readLine()` vs `Files.readAllLines()`?

**Candidate**: `readLine()` in a loop is memory-efficient — it processes one line at a time. `readAllLines()` loads the entire file into memory, which works for small files but causes OOM for large ones. For large files, use `BufferedReader` with streaming.

**Interviewer**: How does `DataOutputStream` ensure portability?

**Candidate**: It uses a fixed binary format: `writeInt()` writes 4 bytes big-endian, `writeDouble()` uses IEEE 754, `writeUTF()` uses a modified UTF-8 with a 2-byte length prefix. This format is platform-independent — a file written on a big-endian Solaris machine can be read on a little-endian x86 Windows machine.

**Interviewer**: What is the `PushbackInputStream` and when would you use it?

**Candidate**: It allows "unreading" bytes back into the stream. This is useful for parsers that need to look ahead: you read a byte, decide it doesn't belong to you, and push it back. For example, in an XML parser, after reading `<`, you might need to decide whether it's a tag or a comment (`<!--`).

**Interviewer**: Design a tail utility. Read the last 10 lines of a large file efficiently.

**Candidate**: I'd use `RandomAccessFile` seeking to near the end, then read backwards in chunks, counting newlines. Once I find 10 newlines, read forward from that position.
