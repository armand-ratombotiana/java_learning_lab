# Caching - VISUAL GUIDE

## Caching Strategy Comparison

```
                        Cache-Aside
Client ──► Check Cache ──┬── Hit ──► Return
                         └── Miss ──► Load DB ──► Populate Cache ──► Return


                        Read-Through
Client ──► Read-Through Cache ──┬── Hit ──► Return
                                └── Miss ──► Load DB (by cache) ──► Return


                        Write-Through
Client ──► Write Cache ──► Write DB ──► Return OK


                        Write-Behind
Client ──► Write Cache ──► Return OK (async queue → DB)


                        Refresh-Ahead
                       (before expiry)
Client ──► Read Cache ───┬── Fresh ──► Return immediately
                         └── Near expiry ──► Return stale + async refresh
```

## Multi-Layer Cache Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Client                               │
└─────────────────────────┬───────────────────────────────────┘
                          │
                    ┌─────▼──────┐
                    │  Browser   │                            L0
                    │  Cache     │   Cache-Control, ETag
                    └─────┬──────┘
                          │
                    ┌─────▼──────┐
                    │  CDN       │                            L1
                    │  (Edge)    │   CloudFront, Cloudflare
                    └─────┬──────┘
                          │
                    ┌─────▼──────┐
                    │  API       │                            L2
                    │  Gateway   │   Response caching
                    └─────┬──────┘
                          │
                    ┌─────▼──────┐
                    │  L1 Cache  │                            L3
                    │  (Local)   │   Caffeine (per instance)
                    └─────┬──────┘
                          │
                    ┌─────▼──────┐
                    │  L2 Cache  │                            L4
                    │ (Distr.)   │   Redis / Memcached
                    └─────┬──────┘
                          │
                    ┌─────▼──────┐
                    │  Database  │                            L5
                    └────────────┘
```

## Cache Eviction Policies

```
LRU (Least Recently Used):
Access order: [C, A, B, D, E]  ← recently used
Evict: oldest access (might be C)

LFU (Least Frequently Used):
Frequency map: {A:10, B:5, C:3, D:2, E:1}
Evict: lowest count (E)

FIFO (First In, First Out):
Insert order: [A, B, C, D, E]
Evict: oldest insertion (A)

TTL (Time To Live):
Expiry times: {A:10:05, B:10:10, C:10:03, D:10:12, E:10:01}
Evict: nearest expiry (E at 10:01)
```

## Cache Invalidation Flow

```
┌──────┐   Write   ┌──────────┐   Invalidate   ┌──────┐
│Client│ ────────► │  Service │ ──────────────► │Cache │
└──────┘           └──────────┘                  └──────┘
                         │
                         │ Write
                         ▼
                    ┌──────────┐
                    │   DB     │
                    └──────────┘

Next Read: Cache miss → Load from DB → Repopulate cache
```
