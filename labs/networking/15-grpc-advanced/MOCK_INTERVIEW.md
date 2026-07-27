# gRPC Advanced — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: Explain gRPC's HTTP/2 usage in detail. How do HTTP/2 frames map to gRPC messages?

**Expected coverage**: gRPC uses HTTP/2 streams for multiplexing, DATA frames carry gRPC payload (length-prefixed message: 5-byte header (1 byte compressed flag + 4 bytes message length) + protobuf payload), HEADERS frame carries gRPC metadata (call definition, path: /package.Service/Method, authority, content-type: application/grpc), PING for keepalive (HTTP/2 PING), GOAWAY for graceful shutdown, RST_STREAM for stream cancellation.

**Q2**: How does gRPC channel management work? Explain subchannels, health checking, and connection reuse.

**Expected coverage**: Channel = logical connection to a target (service name), resolved to addresses, subchannel = TCP connection to specific backend. Health checking: gRPC Health Checking Protocol (gRPC service /grpc.health.v1.Health/Check), subchannel status tracked (READY, IDLE, CONNECTING, TRANSIENT_FAILURE, SHUTDOWN). Connection reuse: multiple RPCs share same subchannel, load balanced across ready subchannels, pick_first (first ready) or round_robin.

**Q3**: What are gRPC interceptors? How do you implement cross-cutting concerns?

**Expected coverage**: Client interceptor (intercepts outgoing calls: logging, auth token injection, retry, timeout), Server interceptor (incoming calls: auth validation, rate limiting, tracing context extraction, request validation). Unary interceptor (single request/response), Stream interceptor (streaming calls). In Go: UnaryClientInterceptor, StreamClientInterceptor. Chain multiple interceptors for layered processing.

## Intermediate (3 questions)

**Q4**: Explain gRPC load balancing in detail. How does xDS-based routing work?

**Expected coverage**: gRPC's built-in load balancing: pick_first (connect to first address, failover to next), round_robin (distribute across ready subchannels), weighted_target (percentage to backends). xDS (Envoy's discovery API): gRPC can use xDS APIs (LRS, CDS, EDS, RDS, LDS) for advanced traffic management — traffic splitting, circuit breaking, outlier detection, locality-weighted distribution. Supported by gRPC-Go, gRPC-Java, gRPC-Core (envoy proxy for non-native languages).

**Q5**: How does gRPC handle retries and timeouts? Explain retry policy configuration.

**Expected coverage**: Retry built into gRPC (service config): maxAttempts, initialBackoff, maxBackoff, backoffMultiplier, retryableStatusCodes (UNAVAILABLE, RESOURCE_EXHAUSTED, DEADLINE_EXCEEDED). Hedging (send multiple clones, use first response). Per-call deadlines propagated to downstream. Timeout at each hop: deadline - elapsed_time, gRPC cancels streams on deadline exceeded. Exponential backoff with jitter for retries.

**Q6**: gRPC vs REST for microservices: which scenarios favor each?

**Expected coverage**: gRPC favor: internal services (high throughput, low latency, streaming), polyglot environments (code gen for all languages), real-time features (bidirectional streaming), strict typing. REST favor: public APIs (browser-friendly, simpler debugging), caching critical (HTTP cache headers), third-party integration (ubiquitous HTTP semantics), when you need simplicity over performance. Many orgs use both: gRPC internally, REST (via gRPC-gateway) externally.

## Advanced (2 questions)

**Q7**: Design a gRPC-based real-time bidding system (500K bids/second) with sub-50ms response requirement.

**Expected coverage**: Bidirectional streaming gRPC (client sends bids, server streams responses), connection pooling (pre-warm subchannels), multiplexed over single HTTP/2 connection, deadline propagation (bid response deadline 50ms), ignore DEADLINE_EXCEEDED bids, async non-blocking I/O, connection keepalive (prevent TCP connection close), flow control (per-stream backpressure), channel pooling for high throughput, custom load balancer (least loaded backend based on CPU/mem metrics).

**Q8**: Your gRPC service is experiencing high latency and connection errors. Walk through end-to-end debugging.

**Expected coverage**: gRPC-level: check status codes (UNAVAILABLE = connection issue, RESOURCE_EXHAUSTED = flow control/congestion, DEADLINE_EXCEEDED = server slow), grpcurl health check, gRPC reflection for service inspection, channelz (gRPC debug tool: subchannels, sockets, resolution). HTTP/2-level: tcpdump analyze HTTP/2 frames (GOAWAY, RST_STREAM, PING RTT), check for stream concurrency limits (default 100 per connection). Network-level: check subchannel connectivity, DNS resolution, latency, firewall blocking HTTP/2, verify ALPN negotiation (h2).
