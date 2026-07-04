# Distributed ID Generation: Visual Guide

## Snowflake ID Structure

```
   0         41 bits         10 bits     12 bits
┌───┬─────────────────────┬────────────┬────────────┐
│ 0 │    Timestamp        │  Worker ID │  Sequence  │
│   │  (ms since epoch)   │  (1024 max)│ (4096/ms)  │
└───┴─────────────────────┴────────────┴────────────┘
```

## UUID Version Comparison

```
Version 1: TTTTTTTT-TTTT-1TTT-AAAA-AAAAAAAAAA
           (time)    (version)  (node/MAC)

Version 4: RRRRRRRR-RRRR-4RRR-BRRR-RRRRRRRRRRRR
           (random)   (version)  (variant)

Version 7: TTTTTTTT-TTTT-7TTT-RRRR-RRRRRRRRRRRR
           (timestamp) (version) (random)
```

## ID Sort Order

```
Snowflake IDs (monotonically increasing):
  1700000000001
  1700000000002
  1700000000003  ← IDs sort by time
  1700000000004

UUID v4 (random):
  2d5e4376-8b3a-4...
  7c1a9e83-2f4d-4...
  a3b8f912-6c7d-4...  ← No sort order
  f12e45a0-89bc-4...
```
