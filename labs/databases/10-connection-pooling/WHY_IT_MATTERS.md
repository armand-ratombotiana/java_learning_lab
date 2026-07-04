# Why Connection Pooling Matters

Connection pooling is critical for database-driven applications because it:

- **Reduces latency**: Eliminates connection setup overhead (5-50ms per request)
- **Increases throughput**: More requests per second with fewer resources
- **Prevents database overload**: Caps concurrent connections to the database
- **Manages spikes**: Handles traffic bursts with pre-warmed connections
- **Provides resilience**: Detects and evicts broken/stale connections
- **Offers observability**: Metrics for pool utilization, wait times, timeouts
- **Prevents connection leaks**: Leak detection tracks unreturned connections
