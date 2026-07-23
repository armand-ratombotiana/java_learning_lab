# Lab 06 — Deployment Rollback: LeetCode Connections

**Q1: Design Circular Queue (LeetCode 622)**
Connection: Rollback is like a circular queue — you need to know the "previous" state to roll back to. Understanding circular buffers helps design deployment version management with N-revision rollback.

**Q2: Min Stack (LeetCode 155)**
Connection: Min Stack tracks minimum in O(1). Deployment rollback needs to know the previous "known good" state. Like Min Stack tracks min, deployment history tracks "known good" revision.

**Q3: LRU Cache (LeetCode 146)**
Connection: LRU Cache eviction is like deployment revision management. The most recently deployed version is the "most recently used." Rollback reverts to the previous version (LRU).

**Q4: Design HashMap (LeetCode 706)**
Connection: Feature flags are like a hash map — key = feature name, value = on/off. Rollback via feature flag is O(1) — flip the bit. Designing efficient feature flag systems maps to hash map design.

**Q5: Clone Graph (LeetCode 133)**
Connection: Blue-green deployment clones the entire environment. Clone Graph algorithm (deep copy with visited tracking) is conceptually what blue-green does — create an identical copy, switch traffic.
