# Visual Guide

## Streaming Topology
```
[Kafka Source] -> [Map: Parse] -> [KeyBy] -> [Window] -> [Aggregate] -> [Sink]
```

## Watermark Progress
```
Event Time --> 10:00  10:01  10:02  W=10:02  10:03  W=10:03
```
