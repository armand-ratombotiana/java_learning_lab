# Architecture: Vector Database

## High-Level Architecture
```
[REST API] --> [Router] --> [Collection Manager]
                                |
            +-------------------+-------------------+
            |                   |                   |
    [HNSW Index]     [Metadata Store]      [Vector Store]
            |                   |                   |
    [Memory-Mapped]      [Inverted Index]    [WAL + Checkpoint]
            |                   |                   |
    [Distance Calc]     [Filter Engine]     [Recovery Manager]
```

## Technology Stack
- **Language**: Java 17 (Project Panama Vector API)
- **Framework**: Spring Boot 3.x (REST API)
- **Build**: Maven
- **Storage**: Memory-mapped files (MappedByteBuffer), WAL on disk
- **Serialization**: Protocol Buffers for WAL records
- **Concurrency**: ReadWriteLock, ConcurrentHashMap, ForkJoinPool
- **Containerization**: Docker
- **Monitoring**: Micrometer + Prometheus

## API
- `POST /collections` — Create collection
- `DELETE /collections/{name}` — Drop collection
- `PUT /collections/{name}/vectors` — Insert/update vector
- `POST /collections/{name}/search` — Search nearest neighbors
- `GET /collections/{name}/vectors/{id}` — Get vector by ID
- `DELETE /collections/{name}/vectors/{id}` — Delete vector
