# Replication: Visual Guide

## Single-Leader Topology

```
                   ┌─────────┐
                   │  Client │
                   └────┬────┘
                        │
                   ┌────▼────┐
                   │  Leader │
                   │(Writes) │
                   └────┬────┘
                        │
        ┌───────────────┼───────────────┐
        │               │               │
   ┌────▼────┐    ┌────▼────┐    ┌────▼────┐
   │Follower │    │Follower │    │Follower │
   │ (Reads) │    │ (Reads) │    │ (Reads) │
   └─────────┘    └─────────┘    └─────────┘
```

## Multi-Leader Topology

```
   ┌─────────┐        ┌─────────┐
   │Leader A │◀──────▶│Leader B │
   │  US-E   │        │  EU-W   │
   └────┬────┘        └────┬────┘
        │                  │
   ┌────▼────┐        ┌────▼────┐
   │Replica  │        │Replica  │
   └─────────┘        └─────────┘
```
