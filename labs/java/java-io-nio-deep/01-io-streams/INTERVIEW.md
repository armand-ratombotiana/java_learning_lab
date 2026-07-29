# Interview Questions: I/O Streams

## Company-Specific Focus

### Amazon
- Design a log file parser that reads large files efficiently using BufferedInputStream
- How would you implement a reverse file reader?

### Google
- Compare byte streams vs character streams; when to use each?
- Encoding issues: how does InputStreamReader handle charset conversion?

### Microsoft
- Decorator pattern in I/O: how BufferedInputStream wraps InputStream
- Memory-mapped files vs BufferedInputStream performance trade-offs

### Meta
- PushbackInputStream use cases in parsing
- SequenceInputStream for concatenating multiple data sources

### Oracle
- try-with-resources and AutoCloseable contract
- Why InputStream/OutputStream are abstract classes vs interfaces

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| Serialize and Deserialize Binary Tree | Hard | Amazon, Google | I/O patterns for tree encoding |

## Real Production Scenarios
1. **Reading a CSV file**: BufferedReader.readLine() vs streaming with custom parser
2. **Network protocol parsing**: PushbackInputStream for lookahead
3. **File concatenation**: SequenceInputStream for merging log files
