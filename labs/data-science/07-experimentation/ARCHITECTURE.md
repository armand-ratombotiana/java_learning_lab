# Architecture: Experimentation Platform

## System Design

```
┌──────────┐    ┌──────────────┐    ┌────────────┐    ┌──────────────┐
│  Traffic  │───>│  Assignment  │───>│  Event     │───>│  Analysis    │
│  Router   │    │  Service     │    │  Tracking  │    │  Pipeline    │
└──────────┘    └──────────────┘    └────────────┘    └──────────────┘
                                                  │
                                                  ├──> Real-time dashboard
                                                  ├──> Weekly report
                                                  └──> Experiment registry
```

## Component Responsibilities

### Assignment Service
- Deterministic assignment via hash (user_id + experiment_id)
- Configurable traffic splits (50/50, 10/90, etc.)
- Supports layered experiments (different dimensions don't interact)
- O(1) latency, no database dependency

```java
public class AssignmentService {
    private static final int BUCKETS = 1000;
    
    public int getBucket(String userId, String experimentId) {
        String key = userId + ":" + experimentId;
        int hash = Hashing.murmur3_32().hashString(key).asInt();
        return Math.floorMod(hash, BUCKETS);
    }
    
    public boolean isTreatment(String userId, Experiment exp) {
        int bucket = getBucket(userId, exp.getId());
        return bucket < (int)(BUCKETS * exp.getTrafficSplit());
    }
}
```

### Event Tracking
- Captures user actions and associates them with experiment assignments
- Handles late-arriving events (user assigned to experiment, converts 7 days later)
- Stores events in a time-series database

### Analysis Pipeline
- Batch (nightly) + real-time (streaming) computation
- Computes per-experiment, per-metric statistics
- Handles CUPED variance reduction
- Detects and alerts on metric anomalies

### Experiment Registry
- Repository of all experiments (past, running, planned)
- Schema: experiment_id, name, hypothesis, owner, status, start_date, end_date, sample_size, results
- Prevents overlapping experiments on the same metric

## Layered Experiment Architecture

```
Layer 1 (UI):    Experiment A (button color)
Layer 2 (ML):    Experiment B (recommendation algorithm)
Layer 3 (Pricing): Experiment C (pricing page)

Different layers are independent — a user can be in experiments
A, B, and C simultaneously as long as they don't share metrics.
```
