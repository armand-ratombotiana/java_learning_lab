# Architecture: Distributed Cache

## High-Level Architecture
```
[Client App] <--> [Client Library (consistent hashing)]
                        |
      +-----------------+-----------------+
      |                 |                 |
[Cache Node A]   [Cache Node B]   [Cache Node C]
      |                 |                 |
[Gossip Manager]  [Hash Ring]     [Replication]
      |                 |                 |
[Eviction Manager] [Cache Engine]  [Transport Layer]
```

## Technology Stack
- **Language**: Java 17
- **Networking**: Netty 4.x (NIO, event loop)
- **Build**: Maven
- **Serialization**: Custom binary protocol (channel buffers)
- **Storage**: In-memory (ConcurrentHashMap) + optional disk persistence
- **Gossip**: UDP multicast (discovery) + TCP (membership sync)
- **Hashing**: MD5 for consistent hash ring
- **Containerization**: Docker + docker-compose
- **Monitoring**: Micrometer + Prometheus

## Client Library Features
- Automatic cluster topology discovery
- Smart routing (direct to primary node)
- Connection pooling
- Automatic failover to replicas
- Pipelining support
