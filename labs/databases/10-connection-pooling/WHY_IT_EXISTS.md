# Why Connection Pooling Exists

Connection pooling exists because opening a database connection is expensive:

1. **TCP handshake**: 1 round trip
2. **TLS/SSL handshake**: 2-3 round trips
3. **Authentication**: 1-2 round trips
4. **Session setup**: Session variables, prepared statement caches

A typical connection open takes 5-50ms (network latency dependent). For a web application serving 100+ requests/second, opening a new connection for every request would waste 50%+ of request time on connection overhead.

Connection pooling reuses connections, reducing this overhead to near zero while also limiting the number of concurrent connections to the database (preventing overload).
