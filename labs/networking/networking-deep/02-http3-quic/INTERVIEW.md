# Interview Questions — HTTP/3 and QUIC

## Beginner

Q: What is QUIC and how does it differ from TCP?
A: QUIC is a UDP-based transport protocol with built-in encryption, 0-RTT, connection migration, and stream multiplexing.

Q: What is 0-RTT and when would you use it?
A: 0-RTT allows sending data immediately on connection. Use for idempotent requests (GET, idempotent POSTs) but not for non-idempotent operations.

## Intermediate

Q: How does QUIC eliminate transport-layer HOL blocking?
A: QUIC multiplexes streams at transport layer. Packet loss on one stream doesn't block other streams.

Q: How does connection migration work in QUIC?
A: Connection IDs allow identifying connection regardless of source IP:port; when network changes, client sends packets from new address with same connection ID.

## Advanced

Q: How does QUIC integrate with TLS 1.3?
A: TLS 1.3 handshake is embedded in QUIC Initial/Handshake packets; handshake records are encrypted; ALPN negotiated; key update mechanism.

Q: Design a migration strategy from HTTP/2 to HTTP/3.
A: Negotiate via Alt-Svc header, fallback to HTTP/2, CDN upgrade path, server-side QUIC listener alongside TCP.
