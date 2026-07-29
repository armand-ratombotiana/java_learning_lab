# Interview Questions: Asynchronous I/O

## Company-Specific Focus

### Amazon
- AsynchronousFileChannel for S3-like storage systems
- CompletionHandler vs Future-based API trade-offs

### Google
- AIO vs NIO for high-throughput services
- Thread pool management for async callbacks

### Microsoft
- Windows IOCP (I/O Completion Ports) and Java AIO mapping
- async/await vs CompletionHandler patterns

### Meta
- Callback hell in CompletionHandler chains
- Structured concurrency with async I/O

### Oracle
- AsynchronousChannelGroup and thread pool configuration
- Why AIO was added in Java 7 despite NIO

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| Design a Thread Pool | Medium | Multiple | Apply to async I/O callback thread pool |

## Real Production Scenarios
1. **Log aggregation service**: async reads from multiple log files
2. **High-throughput file server**: async file reads with callback to network write
3. **Database write-ahead log**: async writes with completion callbacks
