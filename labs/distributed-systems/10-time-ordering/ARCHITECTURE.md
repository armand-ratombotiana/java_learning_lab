# Time and Ordering: Architecture

## Logical Clock Integration

```
Application Layer
    │
    ├──► Logical Clock Module
    │       ├── Lamport/Vector/HLC
    │       └── Clock synchronization
    │
    ├──► Message Layer
    │       └── Attach clock to messages
    │
    └──► Storage Layer
            └── Persist clock with data
```

## Causal Broadcast Architecture

```
Process A                  Process B                  Process C
    │                          │                          │
    │──Message M1─────────────▶│                          │
    │     (clock: (1,0,0))     │                          │
    │                          │                          │
    │                          │──Message M2─────────────▶│
    │                          │     (clock: (1,1,0))     │
    │                          │                          │
    │                          │  (M3 causally after M1)  │
    │◀──────────────────────────│                          │
    │  Message M3 deferred if  │                          │
    │  M1 not yet delivered    │                          │
```

## TrueTime Architecture (Spanner)
```
GPS + Atomic Clock → TimeMaster → timeslave (each server)
    │                                  │
    └────── Clock uncertainty ε ──────┘
                                        │
    Wait ε before making commit visible ──► Consistent snapshot
