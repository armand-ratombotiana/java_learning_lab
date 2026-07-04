# Visual Guide: Connection Pool Lifecycle

```
Application                    HikariCP Pool                   Database
───────────                    ─────────────                   ────────
                                
   getConnection() ──────→  ┌─────────────────┐
                             │  ConcurrentBag   │
                             │  ┌───┐ ┌───┐    │
                             │  │ C1│ │ C2│    │  idle pool
                             │  └───┘ └───┘    │
                             │  ┌───┐          │
                             │  │ C3│          │  in use
                             │  └───┘          │
                             └────────┬────────┘
                                      │
                          ┌───────────┴───────────┐
                          │  HouseKeeper (30s)     │
                          │  ├─ Evict idle > 10min │
                          │  ├─ Evict age > 30min  │
                          │  └─ Fill to min-idle   │
                          └───────────────────────┘

Connection State Machine:
┌──────────┐    borrow()    ┌──────────┐   close()    ┌──────────┐
│  IDLE    │───────────────→│  IN USE  │─────────────→│  IDLE    │
└──────────┘                └──────────┘              └──────────┘
     │                                                    │
     │ evict()                maxLifetime                 │ evict()
     ▼                        expired                     ▼
┌──────────┐                                          ┌──────────┐
│ REMOVED  │                                          │ REMOVED  │
└──────────┘                                          └──────────┘
```
