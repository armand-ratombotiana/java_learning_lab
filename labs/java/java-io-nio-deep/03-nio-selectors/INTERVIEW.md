# Interview Questions: NIO Selectors

## Company-Specific Focus

### Amazon
- Design a scalable chat server using Selectors
- How many connections can one Selector thread handle?

### Google
- Reactor vs Proactor pattern for network services
- Edge-triggered vs level-triggered notifications

### Microsoft
- .NET IOCP vs Java NIO Selectors comparison
- Thread pool sizing with Selector-based architectures

### Meta
- Handling partial reads/writes with non-blocking channels
- Interest op set management for performance

### Oracle
- Selector.select() vs selectNow() vs select(timeout)
- Key cancellation and channel closing best practices

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| Web Crawler Multithreaded | Medium | Amazon, Google | Non-blocking network I/O |

## Real Production Scenarios
1. **HTTP server**: handle thousands of connections with one thread
2. **WebSocket gateway**: multiplex connections with Selector
3. **Reverse proxy**: reading from upstream, writing to downstream using Selector
