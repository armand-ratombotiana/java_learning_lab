# Visual Guide to Real-Time Feature Store

```
Feature Store Architecture:
[Source Data] -> [Feature Engineering] -> [Feature Store]
    |                                     |          |
[Transaction DB]                   [Online Store]  [Offline Store]
[Customer DB]                      [Redis/Cass]   [S3/BigQuery]
    |                                     |          |
[Streaming: Kafka]              [Serving API: REST/gRPC]  [Training Data]
                                        |               |
                                 [ML Inference]    [Model Training]

Point-in-Time Join:
Label @ T1 -> Features BEFORE T1 (correct)
Label @ T1 + Feature @ T2 (T2 > T1) -> DATA LEAKAGE (wrong!)

Feature Timeline:
t0    t1    t2    t3    t4    t5
[e1]  [e2]  [e3]  [e4]  [e5]
  |            |
Feature v1    Label L1
  |            |
  v            v
Correct: L1 uses v1 (v1 < L1)
Wrong:   L1 uses v3 (v3 > L1, future data)
```
