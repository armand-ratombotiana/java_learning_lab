# Interview Questions: NIO Channels

## Company-Specific Focus

### Amazon
- FileChannel vs RandomAccessFile: when is each appropriate?
- Scatter/gather for high-throughput data processing

### Google
- Memory-mapped files: performance benefits and drawbacks
- SocketChannel vs traditional Socket: blocking vs non-blocking

### Microsoft
- FileChannel.transferTo()/transferFrom() for zero-copy
- Channel-to-channel transfers for proxy servers

### Meta
- ByteBuffer management: direct vs heap buffers
- Buffer pooling for high-throughput network services

### Oracle
- Why Channel interface vs InputStream/OutputStream?
- FileChannel locking: FileLock and shared/exclusive locks

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 146 LRU Cache | Medium | All | Memory-mapped backing store |

## Real Production Scenarios
1. **File copy**: FileChannel.transferTo() for OS-level zero-copy
2. **HTTP proxy**: scattering request headers and body into separate buffers
3. **Database storage engine**: memory-mapped files for fast B-tree access
