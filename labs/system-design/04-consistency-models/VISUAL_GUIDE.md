# Consistency Models - VISUAL GUIDE

## Consistency Spectrum Visualization

```
LINEARIZABLE (Strongest)
  │  All operations appear instantaneous
  │  Real-time ordering preserved
  │  Ex: Distributed lock, bank accounts
  │
SEQUENTIAL
  │  Each process sees operations in order
  │  Different processes may see different interleavings
  │  Ex: Database with snapshot isolation
  │
CAUSAL
  │  Causally related operations seen in order
  │  Unrelated operations may be reordered
  │  Ex: Social network comments and replies
  │
READ-YOUR-WRITES
  │  Writes are immediately visible to the writer
  │  Others may see stale data
  │  Ex: Session consistency in web apps
  │
MONOTONIC READS
  │  Reads never go backwards in time
  │  Ex: User dashboard with aggregated data
  │
EVENTUAL (Weakest)
  │  All replicas converge given no writes
  │  No ordering guarantees in the meantime
  │  Ex: DNS, CDN cache
```

## CAP Trade-off Visualization

```
        Network Partition Occurs
        ┌─────────────────────┐
        │                     │
        ▼                     ▼
┌───────────────┐     ┌───────────────┐
│ CP System      │     │ AP System      │
│ (Choose C)     │     │ (Choose A)     │
├───────────────┤     ├───────────────┤
│ Reject writes │     │ Accept writes │
│ to minority   │     │ on both sides │
│ side          │     │               │
│               │     │ Merge on      │
│ Return stale  │     │ recovery      │
│ reads to keep │     │               │
│ consistency   │     │ May have      │
└───────────────┘     │ conflicts     │
                      └───────────────┘
```

## Quorum Read/Write

```
N = 3, W = 2, R = 2  (W + R = 4 > N = 3)

Write:                                            Read:
┌──┐ ┌──┐ ┌──┐                                   ┌──┐ ┌──┐ ┌──┐
│OK│ │OK│ │--│  ← 1 replica missed               │A1│ │A2│ │A2│
└──┘ └──┘ └──┘                                   └──┘ └──┘ └──┘
   \  /                                               \  /
  W=2 ACKs → write committed                       Latest version = A2

Write quorum (2/3) achieved                     Read quorum (2/3) achieved
                                                    Latest version found
```

## Raft Consensus Flow

```
┌──────────────────────────────────────────────┐
│                CLIENT                        │
│  Write Request                               │
└────────┬─────────────────────────────────────┘
         │
┌────────▼──────────┐
│   LEADER (Node 1) │
│   ┌─────────────┐ │
│   │ Log Entry 1 │ │────► AppendEntries ─────┐
│   │ Log Entry 2 │ │────► AppendEntries ─────┤
│   └─────────────┘ │                         │
└───────────────────┘                         │
         │       │                             │
┌────────▼──┐ ┌──▼────────┐     ┌────────────▼┐
│ FOLLOWER  │ │ FOLLOWER  │     │  FOLLOWER   │
│ (Node 2)  │ │ (Node 3)  │     │  (Node 4)   │
│ Append OK │ │ Append OK │     │  Append OK  │
└───────────┘ └───────────┘     └─────────────┘
         │       │                    │
         └───────┴───────┬────────────┘
                         │
                    Majority ACK → Commit
```
