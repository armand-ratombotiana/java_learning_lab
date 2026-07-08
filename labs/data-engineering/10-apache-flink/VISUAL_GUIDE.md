# Visual Guide to Apache Flink

```
Flink Architecture:
+-----------+
| JobManager| [Scheduler] [CheckpointCoordinator] [HA]
+-----------+
    |
+-----------v-----------+
|    TaskManager 1       |
| [Slot1] [Slot2] [Slot3]|
| DataStream operators   |
+-----------------------+

Watermark Timeline:
Events:  e1   e2   e3   e4   e5
Time:    t1   t3   t4   t2   t7
                          ↑
                     e4 late
Watermark: w(t3)   w(t4)     w(t7)
```
