# Caching - ARCHITECTURE

## Enterprise Caching Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Application Layer                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Auth     │  │ Product  │  │ Order    │  │ Search   │  │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  │
│       │             │             │             │          │
└───────┼─────────────┼─────────────┼─────────────┼──────────┘
        │             │             │             │
        ▼             ▼             ▼             ▼
┌─────────────────────────────────────────────────────────────┐
│                    Cache Abstraction Layer                    │
│                                                              │
│  ┌──────────────────┐    ┌──────────────────┐              │
│  │ Local Cache Pool │    │ Distributed      │              │
│  │ (Caffeine)       │    │ Cache (Redis)    │              │
│  ├──────────────────┤    ├──────────────────┤              │
│  │ Hot data (1%)    │    │ Warm data (10%)  │              │
│  │ Per-instance     │    │ Shared across    │              │
│  │ < 100ms TTL      │    │ instances        │              │
│  └──────────────────┘    └──────────────────┘              │
└─────────────────────────────────────────────────────────────┘
        │             │             │             │
        ▼             ▼             ▼             ▼
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │PostgreSQL│  │Elastic   │  │ Kafka    │  │ S3       │  │
│  │(source)  │  │(search)  │  │(events)  │  │(blobs)   │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## Redis Cluster Topology

```
         ┌─────────────────────────────────────┐
         │        Redis Cluster (6 nodes)       │
         │                                       │
         │  ┌──────────┐  ┌──────────┐         │
         │  │  Node 1  │  │  Node 2  │  Node 3 │
         │  │  Master  │  │  Master  │  Master  │
         │  │ Slots    │  │ Slots    │  Slots   │
         │  │ 0-5460   │  │ 5461-10922│ 10923-  │
         │  └────┬─────┘  └────┬─────┘ └───┬────┘
         │       │             │            │
         │  ┌────▼─────┐  ┌────▼─────┐ ┌───▼────┐
         │  │  Repl A  │  │  Repl B  │ │ Repl C │
         │  └──────────┘  └──────────┘ └────────┘
         └────────────────────────────────────────┘
```

## Cache Strategy Per Data Type

| Data Type | Strategy | Cache | TTL | Reason |
|-----------|----------|-------|-----|--------|
| Product catalog | Cache-Aside | Redis | 30 min | Read-heavy, rarely changes |
| User session | Write-Through | Redis | 24 hours | Need consistency |
| Trending products | Refresh-Ahead | Caffeine | 1 min | Rapidly changing |
| Search results | Cache-Aside | Redis | 5 min | Computationally expensive |
| Price data | Write-Through | Redis | 10 min | Accuracy important |
| Static content | CDN | Edge | 24 hours | No invalidation needed |
