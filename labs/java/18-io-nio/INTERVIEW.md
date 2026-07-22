# Interview Questions: I/O & NIO

## Company-Specific Focus

### Google
- Blocking vs non-blocking I/O in the JVM: performance characteristics
- NIO Selectors: single thread handling multiple channels and efficient I/O
- Memory-mapped files: performance and allocation advantages

### Microsoft
- Java NIO vs C# async/await: comparison of asynchronous patterns
- NIO messages: handling encoding and decoding for multi-type messages
- Java NIO and SSL/TLS sockets: using SSLEngine for encryption

### Amazon
- NIO Channel for the network interface card: high scale usage
- Scatter/Gather I/O with multibyt e buffer for network, large file IO System
- File locking and transfer in distributed systems with NIO

### Meta
- File processing: use FileChannel and MappedByteBuffer
- ByteBuffer vs Strin g: the memory layout for an encoding
- Data Mappings for the on-spatial allocation of common DP patterns

### Apple
- Efficient NIO direct buffer for its smaller memory consumption
- Using a buffer management system throughout lifecycles
- Using wait queues from the selectors approach for better performance

### Oracle
- Generic TCP Pools: Java NIO architecture principal for non-blocking actions
- Direct, non-direct buffer: different allocation and JVM behavior
- NIO socket channel vs API standard

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 273 Integer to English Words | Hard | Amazon, Google | String pattern processing IO approach |
| 539 Minimum Time Difference | Medium | Apple, Google, Amazon | I/O process into numbers |
| 68 Text Justification | Hard | Facebook, Amazon, Apple | Sentence writing logic in block |
| 6 Zigzag Conversion | Medium | Google, Microsoft | I/O reading reading pattern |
| 165 Compare Version Numbers | Medium | Amazon, Apple, Microsoft, Google | String number of parse character |

## Real Production Scenarios
- **Amazon**: A NIO buffer in a microservice in persistent read set cause a faulty frame in read after it was consumed to the peek pattern fixed
- **Cloudflare**: Using MappedByteBuffer, resulting direct memory consumption unbounded because a page was not returned to the OS.
- **The I/O**: I/O socket data read failure because of incorrect handling of partial reads in blocking mode

## Interview Patterns & Tips
- **flush vs commit**: Writing data to buffers directly to streams is not the same as writing to the underlying transport
- **Buffer flipping**: Calling flip() to set the limit and position; calling clear/reset/capacity accordingly
- **Selector size handling**: More threads can choose from the se, but you need to avoid thread safety problems

## Deep Dive Questions
- **Buffer allocation**: Heap buffer vs Direct buffer: what advantages do they have
- **JVM memory**: How does the JVM allocate memory for the direct byte buffer?
- **Channel interop**: How does a SocketChannel with Selector interact with the event system?
- **I/O via the GC**: What are the impact of large heap buffers versus arrays of small objects?
- **JNI**: Where does the native code's I/O handle buffers?