# Performance — I/O & NIO

## Buffer Sizes
| Buffer | Size | Use |
|--------|------|-----|
| `BufferedInputStream` default | 8 KB | General I/O |
| `ByteBuffer.allocateDirect` | 8-64 KB | NIO channels |
| `MappedByteBuffer` | File size | Large files |

## Zero-Copy Performance
`FileChannel.transferTo()` outperforms manual read+write by 2-5x for large files.

## Direct vs Heap ByteBuffer
- Direct allocation: ~100x slower to allocate but zero-copy I/O
- Heap allocation: ~100x faster to allocate but extra copy for I/O
- Sweet spot: allocate direct buffers once, reuse them

## FileChannel vs Streams
```
FileChannel.read() → ~1-2 µs per 4 KB
BufferedInputStream.read() → ~2-5 µs per 4 KB
```

## Memory-Mapped Files
- Best for: large files (MB-GB), random access patterns
- Worst for: small files (allocation overhead), sequential reads

## I/O Profiling
Use `-XX:+PrintGCDetails` — excessive GC may indicate byte[] churn. Use direct buffers.
