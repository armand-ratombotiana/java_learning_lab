# Visual Guide

## Architecture
```
+--------+  +--------+  +--------+  +--------+
| Driver |  | Exec 1 |  | Exec 2 |  | Exec 3 |
+--------+  +--------+  +--------+  +--------+
| DAG     |  | Cache  |  | Cache  |  | Cache  |
| Sched   |  | Tasks  |  | Tasks  |  | Tasks  |
+--------+  +--------+  +--------+  +--------+
```

## DAG
```
Stage 0: Read + Filter (narrow)
Stage 1: Shuffle + Aggregate (wide)
Stage 2: Sort (wide)
```
