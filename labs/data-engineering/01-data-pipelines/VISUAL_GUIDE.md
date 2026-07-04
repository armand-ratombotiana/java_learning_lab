# Visual Guide

## Pipeline Architecture
```
[Source] -> [Extract] -> [Transform] -> [Load] -> [Warehouse]
```

## State Machine
PENDING -> RUNNING -> STOPPING -> COMPLETED
                               -> FAILED -> TIMEOUT -> ABORTED

## Streaming Pipeline
```
[Producers] -> [Kafka] -> [Stream Processor] -> [Consumers]
```
