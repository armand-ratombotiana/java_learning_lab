# HTTP/3 & QUIC — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: What is QUIC? How does it differ from TCP?

**Expected coverage**: QUIC: encrypted transport protocol developed by Google, RFC 9000, runs over UDP. Key differences: built-in encryption (TLS 1.3 mandatory), 0-RTT connection establishment, connection migration (survive IP/port changes), stream multiplexing without head-of-line blocking, faster handshake (1-RTT, 0-RTT for repeat), userspace implementation (faster iteration).

**Q2**: Explain how QUIC eliminates head-of-line blocking compared to HTTP/2 over TCP.

**Expected coverage**: HTTP/2 multiplexes streams over a single TCP connection. TCP is a reliable, in-order byte stream — if one packet is lost, ALL streams block until retransmission. QUIC multiplexes streams independently over UDP — each stream has its own sequence number space. A lost packet only blocks its own stream. Other streams continue uninterrupted.

**Q3**: Walk through the QUIC handshake (1-RTT and 0-RTT).

**Expected coverage**: 1-RTT: Client sends Initial (CRYPTO frame: ClientHello), Server responds with Initial + Handshake (ServerHello, TLS cert, transport parameters), Client sends Handshake (finished), both can send 1-RTT data. 0-RTT (repeat connection): Client sends Initial + 0-RTT (application data immediately with cached server config). Server responds with Handshake + 1-RTT data. Replay protection for 0-RTT (idempotent requests only).

## Intermediate (3 questions)

**Q4**: How does QUIC handle connection migration? What are the security implications?

**Expected coverage**: Connection identified by Connection ID (not IP:port), client changes network (Wi-Fi → cellular) → continues with same CID, server sees new IP/port but same CID → connection continues. Security: CID must be unpredictable (prevent off-path injection), source address validation (address migration requires NEW_TOKEN/challenge-verify to prevent amplification attacks by spoofing migrated IP).

**Q5**: Explain QUIC's flow control mechanism.

**Expected coverage**: Two levels: stream-level flow control (max_data per stream, stream-specific limits) and connection-level flow control (max_data for all streams combined). Credit-based: receiver grants credits via MAX_DATA/MAX_STREAM_DATA frames. Sender cannot exceed available credit. Auto-tuning similar to TCP window scaling. Prevents any single stream from consuming all connection capacity.

**Q6**: How does HTTP/3 differ from HTTP/2 at the application layer?

**Expected coverage**: HTTP/3 is HTTP over QUIC (not QUIC itself). Key differences: QPACK (out-of-order header compression, replaces HPACK's ordering requirement), server push (deprecated in HTTP/3, removed in some implementations), Extensible Priorities (RFC 9218, simpler than HTTP/2's priority tree), no more TCP-related issues (head-of-line, migration). HTTP semantics unchanged: same methods, headers, status codes.

## Advanced (2 questions)

**Q7**: Compare QUIC termination and load balancing with TCP termination. What challenges does QUIC introduce?

**Expected coverage**: TCP termination: TCP connection → TLS → HTTP/2. QUIC termination: QUIC connection → HTTP/3. Challenges: Connection migration (handoff between LB instances), 0-RTT replay (LB must detect and drop duplicates), connection affinity (QUIC CID routing, hash CID consistently vs sticky by IP). Solutions: L4 LB passes QUIC intact to backend, QUIC LB (e.g., Cloudflare Unimog) routes by CID hash, connection migration token for re-routing.

**Q8**: Your HTTP/3 adoption is low because firewalls block UDP 443. How do you migrate gracefully?

**Expected coverage**: Detect QUIC capability via ALT-SVC header (Alt-Svc: h3=":443"), TCP fallback on failure (browser retries HTTP/2 or HTTP/1.1), negotiate-version (QUIC v1 vs v2), incremental rollback (monitor QUIC error rates by provider, disable QUIC per region if needed), monitor UDP blocking (QUIC handshake failures indicate blocking), educate network teams (open UDP 443 for improved performance), consider TCP termination as fallback strategy.
