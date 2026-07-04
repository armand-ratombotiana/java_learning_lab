# Distributed Locking: Visual Guide

## ZooKeeper Lock Sequence

```
Lock Request: create /locks/mylock/lock-0000000003

Nodes in /locks/mylock:
  lock-0000000001 (holds lock)
  lock-0000000002 (watching 1)
  lock-0000000003 (watching 2) ← us

When lock-0000000001 deletes:
  lock-0000000002 gets lock
  lock-0000000003 watches lock-0000000002
```

## Redlock Flow

```
Client                     Redis Instances
  │                    R1    R2    R3    R4    R5
  ├── SET lock NX PX──▶│─────▶│─────▶│─────▶│─────▶│
  │◀───────────────OK──│◀────│◀────│◀────│◀────│
  │                                              │
  │ [Majority acquired = 3/5 = R1,R2,R3]         │
  │ [Lock acquired successfully]                  │
  │                                              │
  │──Release (DEL)─────▶│─────▶│─────▶│─────▶│─────▶│
```

## Fencing Token Flow

```
  Client A                    Resource              Client B
    │ Gets lock (token=1)       │                     │
    │                           │                     │
    │ [GC pause - 30 seconds]   │                     │
    │                           │                     │
    │                           │   Lock expires      │
    │                           │                     │─▶ Acquires lock
    │                           │                     │   (token=2)
    │                           │                     │
    │ [GC resumes]              │                     │
    │──Write(token=1)──────────▶│                     │
    │                           │                     │
    │                           │  Rejects:           │
    │                           │  token(1) <= last(2)│
    │                           │                     │
    │                           │◀──Write(token=2)────│
    │                           │  Accepted           │
```
