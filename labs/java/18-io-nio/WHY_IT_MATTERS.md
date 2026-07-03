# Why I/O & NIO Matter

## Universal Data Access
Every application reads/writes data — files, network, stdin/stdout.

## Performance at Scale
- Buffered streams: 100x faster than unbuffered
- NIO channels: zero-copy transfers (`transferTo`, `transferFrom`)
- Memory-mapped: near-instant for large files

## Portability
Write once, read/write on any OS — path separators, line endings, encoding handled automatically.

## Robustness
`try-with-resources` guarantees resource cleanup, preventing resource leaks.

## Modern File I/O
`Files.walk()`, `Files.find()`, `Files.lines()` — integrate with Streams API for concise, powerful file processing.
